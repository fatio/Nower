#coding:utf-8

from MySQL import MySQL
from MyConfig import MyConfig
import logging
import base64
import random
import time
import hashlib
import string
from keyczar import keyczar
import os

########################################################################
class MyModel:
    """模型函数类"""
    
    _db = None;
    _tokens = {'username':'dict(token, user_id)'}
    
    #----------------------------------------------------------------------
    def __init__(self):
        """Constructor"""
        self._db = MySQL(MyConfig.DB_CONFIG)
        self._tokens = {}
    #----------------------------------------------------------------------
    def __del__(self):
        """释放资源"""
        self._db.close()


    #----------------------------------------------------------------------
    def assign_token(self, username): 
        """分派token"""
        #token = 'ffffff'
        try:
            token = base64.b64encode(':'.join([username, str(random.random()), str(time.time()+MyConfig.TOKEN_TIME_OUT)]))
        except Exception, e:
            token = ''
        self._tokens[username] = {'token':token, 'user_id':self.get_user_id(username)}
        logging.error("assign token: "+token) 
        token = token.replace('=', '')
        return token
    
    
# 位置地址相关****************************************************************************************
    #----------------------------------------------------------------------
    def exist_location(self, address):
        """验证地址是否存在"""
        sql = "select * from Locations where address=%s limit 1;"
        n = self._db.execute(sql, [address])
        return n==1
    #----------------------------------------------------------------------
    def get_location_id(self, address):
        """获取location_id"""
        sql = "select location_id from Locations where address = %s limit 1;"
        n = self._db.execute(sql, [address])
        return n==1 and self._db.fetchOne()[0] or '-1'
    #----------------------------------------------------------------------
    def insert_location(self, address, latitude, longitude, description):
        """插入一个地址"""
        sql = "insert into Locations(address, latitude, longitude, description) values(%s, %s, %s, %s);"
        args = [address, latitude, longitude, description]
        n = self._db.execute(sql, args)
        return n==1 
    #----------------------------------------------------------------------
    def upload_location(self, address, latitude, longitude, description):
        """上传地址"""
        jsonstr = {}
        if not address or not latitude or not longitude:
            jsonstr['code'] = -1
            jsonstr['msg'] = u'地址不合规范'
            return jsonstr            
        if not self.exist_location(address):
            if (self.insert_location(address, latitude, longitude, description)):
                jsonstr['code'] = 1
                jsonstr['msg'] = u'插入地址'+address+u'成功'
                return jsonstr  
            else:
                jsonstr['code'] = -2
                jsonstr['msg'] = u'插入地址'+address+u'失败'
                return jsonstr  
        else:
            jsonstr['code'] = -3
            jsonstr['msg'] = u'地址'+address+u'已存在'
            return jsonstr  

# 文件相关
    #----------------------------------------------------------------------
    def get_user_head_pic(self, username):
        """获取用户头像名"""
        sql = "select head_pic from Users where username=%s limit 1;"
        n = self._db.execute(sql, [username])
        return n==1 and self._db.fetchOne()[0] or 'default_head.jpg'
    
    
# 用户登陆相关
    #----------------------------------------------------------------------
    def get_user_id(self, username):
        """获取user_id"""
        sql = "select user_id from Users where username = %s limit 1;"
        n = self._db.execute(sql, [username])
        return n==1 and self._db.fetchOne()[0] or '-1'

    #----------------------------------------------------------------------
    def get_username_by_id(self, user_id):
        """获取user"""
        sql = "select username from Users where user_id = " + str(user_id) + " limit 1;"
        n = self._db.execute(sql)
        return n==1 and self._db.fetchOne()[0] or ''

    #----------------------------------------------------------------------
    def get_user_id_by_show_id(self, show_id):
        """获取user"""
        sql = "select user_id from Shows where show_id = " + str(show_id) + " limit 1;"
        n = self._db.execute(sql)
        return n==1 and self._db.fetchOne()[0] or '-1'



    #----------------------------------------------------------------------
    def exist_username(self, username):
        """验证用户名是否存在"""
        sql = "select * from Users where username=%s limit 1;"
        n = self._db.execute(sql, [username])
        return n==1
    
    #----------------------------------------------------------------------
    def insert_user(self, user):
        """插入一个用户"""
        sql = "insert into Users(username, password, state, sex, age, phone, head_pic, mood) values(%s, %s, %s, %s, %s, %s, %s, %s);"
        args = [user.get('username', ''), user.get('password', ''), 1, user.get('sex', '0'), user.get('age', '0'), user.get('phone', '0'), user.get('head_pic', 'default_head.png'), user.get('mood', u'这家伙太懒了，什么也没说...')]
        n = self._db.execute(sql, args)
        return n==1
    #----------------------------------------------------------------------
    def register(self, username, password, phone, head_pic):
        """注册账号"""
        jsonstr = {}
        if not username:
            jsonstr['code'] = -1
            jsonstr['msg'] = u'没有用户名'
            return jsonstr
        if not password:
            jsonstr['code'] = -2
            jsonstr['msg'] = u'没有密码'
            return jsonstr
        if self.exist_username(username):
            jsonstr['code'] = -3
            jsonstr['msg'] = u'用户名已存在'
            return jsonstr           
        user = {}
        user['username'] = username 
        user['password'] = password
        user['phone'] = phone
        user['head_pic'] = head_pic
        self.insert_user(user)
        jsonstr['code'] = 1
        jsonstr['msg'] = u'注册成功，请登陆...' 
        
        from_user_id = 1
        to_user_id = self.get_user_id(username)
        msg_type = 0 
        message = username+u'，欢迎您的注册！祝你玩得愉快！'
        msg_obj = username
        self.insert_msg(from_user_id, to_user_id, msg_type, message, msg_obj) 
        return jsonstr
     
    #----------------------------------------------------------------------
    def verify_user(self, username, password):
        """验证用户密码"""
        if username == '' or password == '':
            return False
        sql = "select user_id from Users where username = %s and password = %s limit 1;"
        n = self._db.execute(sql, [username, password]) 
        return n==1
    #----------------------------------------------------------------------
    def dePwd(self, password):
        """AES"""
        path = IMAGE_DIR = os.path.join(os.path.split(os.path.realpath(__file__))[0], './rsa_key')
        crypter = keyczar.Crypter.Read(path)
        return crypter.Decrypt(password)
    #----------------------------------------------------------------------
    def login(self, username, password):
        """登陆"""
        logging.error(password)
        password = self.dePwd(password)
        logging.error(password)
        jsonstr = {}
        if not username or not password:
            jsonstr['code'] = -1
            jsonstr['msg'] = u'用户名或密码不能为空啦啦啦啦'
            return jsonstr   
        if not self.exist_username(username):
            jsonstr['code'] = -2
            jsonstr['msg'] = u'用户名不存在，肿么办？'
            return jsonstr
        if not self.verify_user(username, password):
            jsonstr['code'] = -3
            jsonstr['msg'] = u'密码错误了呐...'
            return jsonstr
        jsonstr['code'] = 1
        jsonstr['msg'] = u'登陆成功了，请稍候...'
        jsonstr['token'] = self.assign_token(username)
        
        #to_user_id = self.get_user_id(username)
        #msg_obj = username
        #self.insert_msg(1, to_user_id, 0, u"欢迎您的回归！", msg_obj)
        
        return jsonstr  
    
    #----------------------------------------------------------------------
    def update_password(self, username, phone, password):
        """"""
        jsonstr = {}
        if not self.exist_username(username):
            jsonstr['code'] = -1
            jsonstr['msg'] = u'用户名不存在'
            return jsonstr
        sql = "update Users set password=%s where username=%s and phone=%s;"
        n = self._db.execute(sql, [password, username, phone])
        if n==1: 
            jsonstr['code'] = 1  
            jsonstr['msg'] = u'修改密码成功'
            return jsonstr 
        else:
            jsonstr['code'] = -1  
            jsonstr['msg'] = u'修改密码失败，用户名和手机号不匹配。'
            return jsonstr

    #----------------------------------------------------------------------
    def update_mood(self, username, mood):
        """"""
        jsonstr = {}
        if not self.exist_username(username):
            jsonstr['code'] = -1
            jsonstr['msg'] = u'用户名不存在'
            return jsonstr
        sql = "update Users set mood=%s where username=%s;"
        n = self._db.execute(sql, [mood, username])
        if n==1: 
            jsonstr['code'] = 1  
            jsonstr['msg'] = u'更新心情成功'
            return jsonstr 
        else:
            jsonstr['code'] = -1  
            jsonstr['msg'] = u'更新心情失败'
            return jsonstr

    #----------------------------------------------------------------------
    def update_user_info(self, username, phone, mood, head_pic, sex, age, state):
        """"""
        jsonstr = {}
        if not self.exist_username(username):
            jsonstr['code'] = -1
            jsonstr['msg'] = u'用户名不存在'
            return jsonstr
        sex = int(sex)
        age = int(age)
        state = int(state)
        sql = "update Users set phone=%s, mood=%s, head_pic=%s, sex=" + str(sex) + ", age=" + str(age) + ", state=" + str(state) + " where username=%s;"
        n = self._db.execute(sql, [phone, mood, head_pic, username])
        if n==1: 
            jsonstr['code'] = 1  
            jsonstr['msg'] = u'修改用户信息成功'
            return jsonstr 
        else:
            jsonstr['code'] = -1  
            jsonstr['msg'] = u'修改用户密码失败'
            return jsonstr

    #----------------------------------------------------------------------
    def get_user_info(self, username):
        """获取用户信息"""
        jsonstr = {}
        if not self.exist_username(username):
            jsonstr['code'] = -1
            jsonstr['msg'] = u'用户名不存在'
            return jsonstr
        sql = "select username, phone, head_pic, reg_time, mood, sex, age, state from Users where username=%s limit 1;"
        user = self._db.query_one(sql, [username])
        jsonstr['code'] = 1  
        jsonstr['msg'] = u'成功获取用户信息'
        fields = ['username', 'phone', 'head_pic', 'reg_time', 'mood']
        for i, field in enumerate(fields):
            jsonstr[field] = user[i]
        jsonstr['sex'] = user[5]
        jsonstr['age'] = user[6]
        jsonstr['state'] = user[7]
        return jsonstr
    
    
# shows相关
    #----------------------------------------------------------------------
    def get_show_likes(self, show_id):
        """"""
        sql = "select count(*) from LikeShows where to_show_id=%s;"
        n = self._db.query_one (sql, [show_id])[0]
        return n
    #----------------------------------------------------------------------
    def has_liked(self, show_id, username):
        """"""
        user_id = self.get_user_id(username)
        sql = "select like_time from LikeShows where to_show_id=%s and from_user_id=%s limit 1;"
        n = self._db.execute(sql, [show_id, user_id]) 
        return n==1
    #----------------------------------------------------------------------
    def get_my_shows(self, username, show_id, start, num):
        """获取我的show"""
        show_id = int(show_id)
        start = int(start)
        num = int(num)
        jsonstr = {}
        #logging.error(username)
        if not username or not self.exist_username(username):
            jsonstr['code'] = -1
            jsonstr['msg'] = u'用户名不存在'
            return jsonstr
        shows = []
        #sql = "select Shows.show_id, username, head_pic, show_time, content, images, audios, is_anonymous, address, latitude, longitude from Users, Shows, Locations, WhereUpload where Users.user_id=Shows.user_id and Shows.show_id=WhereUpload.show_id and WhereUpload.location_id=Locations.location_id and username=%s order by Shows.show_id desc limit " + str(start) + ", " + str(num) +";";
        sql = "select Shows.show_id, username, head_pic, show_time, content, images, audios, is_anonymous, address, latitude, longitude from Users, Shows, Locations, WhereUpload where Users.user_id=Shows.user_id and Shows.show_id=WhereUpload.show_id and WhereUpload.location_id=Locations.location_id and username=%s and Shows.show_id<" + str(show_id) + " order by Shows.show_id desc limit " + str(num) +";";
        qshows = self._db.query_all(sql, [username])
        if qshows==None:
            qshows = [] 
        names = ['show_id', 'username', 'head_pic', 'show_time', 'content', 'images', 'audios', 'is_anonymous', 'address', 'latitude', 'longitude']
        length = len(qshows)
        for qshow in qshows: 
            show = {}
            for i, name in enumerate(names):
                show[name] = qshow[i]
            show['likes'] = self.get_show_likes(show['show_id']) 
            show['has_liked'] = self.has_liked(show['show_id'], username)

            sql = "select Users.username, Comments.content, Comments.comment_id, Comments.is_anonymous, Comments.comment_time from Users, CommentShows, Comments where Users.user_id=from_user_id and Comments.comment_id=CommentShows.comment_id and to_show_id="+str(show['show_id'])+";"
            cs = self._db.query_all(sql)
            if cs==None:
                cs = []
            comments = []
            for c in cs:
                comment = {} 
                comment['comment_user'] = c[0]
                comment['commented_user'] = show['username']
                comment['content'] = c[1]
                comment['comment_id'] = c[2]
                comment['is_anonymous'] = c[3]
                comment['comment_time'] = c[4]
                comments.append(comment)
                replys = self.getReplyComments(comment['comment_user'], comment['comment_id'])
                comments.extend(replys)
            show['comments'] = comments 

            shows.append(show)
        jsonstr['data'] = shows
        jsonstr['code'] = 1
        jsonstr['n'] = length;
        jsonstr['msg'] = str(length)+u"条记录已被查询" 
        return jsonstr 

    #----------------------------------------------------------------------
    def get_neighbor_shows(self, address, username0, show_id, start, num):
        """获取附近的show"""
        show_id = int(show_id)
        start = int(start)
        num = int(num)
        jsonstr = {}
        if not address or not self.exist_location(address):
            jsonstr['code'] = -1
            jsonstr['msg'] = u'没有这个地址'
            return jsonstr
        shows = []
        #sql = "select Shows.show_id, username, head_pic, show_time, content, images, audios, is_anonymous, address, latitude, longitude from Users, Shows, Locations, WhereUpload where Users.user_id=Shows.user_id and Shows.show_id=WhereUpload.show_id and WhereUpload.location_id=Locations.location_id and username=%s order by Shows.show_id desc limit " + str(start) + ", " + str(num) +";";
        sql = "select Shows.show_id, username, head_pic, show_time, content, images, audios, is_anonymous, address, latitude, longitude from Users, Shows, Locations, WhereUpload where Users.user_id=Shows.user_id and Shows.show_id=WhereUpload.show_id and WhereUpload.location_id=Locations.location_id and address=%s and Shows.show_id<" + str(show_id) + " order by Shows.show_id desc limit " + str(num) +";";
        #logging.error(sql)
        qshows = self._db.query_all(sql, [address])
        if qshows==None:
            qshows = [] 
        names = ['show_id', 'username', 'head_pic', 'show_time', 'content', 'images', 'audios', 'is_anonymous', 'address', 'latitude', 'longitude']
        length = len(qshows)
        for qshow in qshows:
            show = {}
            for i, name in enumerate(names):
                show[name] = qshow[i]
            show['likes'] = self.get_show_likes(show['show_id'])
            show['has_liked'] = self.has_liked(show['show_id'], username0)

            sql = "select Users.username, Comments.content, Comments.comment_id, Comments.is_anonymous, Comments.comment_time from Users, CommentShows, Comments where Users.user_id=from_user_id and Comments.comment_id=CommentShows.comment_id and to_show_id="+str(show['show_id'])+";"
            cs = self._db.query_all(sql)
            if cs==None:
                cs = []
            comments = []
            for c in cs:
                comment = {} 
                comment['comment_user'] = c[0]
                comment['commented_user'] = show['username']
                comment['content'] = c[1]
                comment['comment_id'] = c[2]
                comment['is_anonymous'] = c[3]
                comment['comment_time'] = c[4]
                comments.append(comment)
                replys = self.getReplyComments(comment['comment_user'], comment['comment_id'])
                comments.extend(replys)
            show['comments'] = comments 
 
            shows.append(show)
        jsonstr['data'] = shows
        jsonstr['code'] = 1
        jsonstr['n'] = length;
        jsonstr['msg'] = str(length)+u"条记录已被查询"  
        return jsonstr 
    

    #----------------------------------------------------------------------
    def get_one_show(self, username0, show_id):
        """获取 a show"""
        show_id = int(show_id)
        jsonstr = {}
        sql = "select Shows.show_id, username, head_pic, show_time, content, images, audios, is_anonymous, address, latitude, longitude from Users, Shows, Locations, WhereUpload where Users.user_id=Shows.user_id and Shows.show_id=WhereUpload.show_id and WhereUpload.location_id=Locations.location_id and Shows.show_id=" + str(show_id) + " limit 1;";
        #logging.error(sql)
        qshow = self._db.query_one(sql)
        if qshow==None:
            qshow = [] 
        names = ['show_id', 'username', 'head_pic', 'show_time', 'content', 'images', 'audios', 'is_anonymous', 'address', 'latitude', 'longitude']
        length = len(qshow)
        if length==0:
            logging.error(show_id)
            jsonstr['code'] = -1 
            jsonstr['n'] = length;
            jsonstr['msg'] = u"没有记录" 
            return jsonstr
        show = {}
        for i, name in enumerate(names):
            show[name] = qshow[i]
        show['likes'] = self.get_show_likes(show['show_id'])
        show['has_liked'] = self.has_liked(show['show_id'], username0)

        sql = "select Users.username, Comments.content, Comments.comment_id, Comments.is_anonymous, Comments.comment_time from Users, CommentShows, Comments where Users.user_id=from_user_id and Comments.comment_id=CommentShows.comment_id and to_show_id="+str(show['show_id'])+";"
        cs = self._db.query_all(sql)
        if cs==None:
            cs = []
        comments = []
        for c in cs:
            comment = {} 
            comment['comment_user'] = c[0]
            comment['commented_user'] = show['username']
            comment['content'] = c[1]
            comment['comment_id'] = c[2]
            comment['is_anonymous'] = c[3]
            comment['comment_time'] = c[4]
            comments.append(comment)
            replys = self.getReplyComments(comment['comment_user'], comment['comment_id'])
            comments.extend(replys)
        show['comments'] = comments 

        jsonstr['data'] = show
        jsonstr['code'] = 1
        jsonstr['msg'] = str(length)+u"条记录已被查询"   
        return jsonstr 
    
    
    
    #----------------------------------------------------------------------
    def upload_show(self, args):
        """"""
        jsonstr={}
        username = args.get('username', '')
        content = args.get('content', '')
        images = args.get('images', '')
        audios = args.get('audios', '')
        is_anonymous = int(args.get('is_anonymous', '0'))
        address = args.get('address', '')
        user_id = self.get_user_id(username)

        sql = "insert into Shows(user_id, content, images, audios, is_anonymous) values(" + str(user_id) + ", %s, %s, %s, " + str(is_anonymous) + ");"
        show_id = self._db.insert_one(sql, [content, images, audios])

        location_id = self.get_location_id(address)
        sql = "insert into WhereUpload(show_id, location_id) values("+str(show_id)+", "+str(location_id)+");"
        self._db.execute(sql) 

        sql = "select Shows.show_id, username, head_pic, show_time, content, images, audios, is_anonymous, address, latitude, longitude from Users, Shows, Locations, WhereUpload where Users.user_id=Shows.user_id and Shows.show_id=WhereUpload.show_id and WhereUpload.location_id=Locations.location_id and username='"+ username +"' and Shows.show_id=" + str(show_id) + " limit 1;"
        qshow = self._db.query_one(sql)
        if qshow==None:
            qshow = []   
        #logging.error(qshow) 
        names = ['show_id', 'username', 'head_pic', 'show_time', 'content', 'images', 'audios', 'is_anonymous', 'address', 'latitude', 'longitude']
        show = {}
        if len(qshow)!=0:
            for i, name in enumerate(names):
                show[name] = qshow[i]
            show['likes'] = self.get_show_likes(show['show_id'])
        jsonstr['show'] = show
        jsonstr['code'] = 1
        jsonstr['n'] = 1; 
        jsonstr['msg'] = str(jsonstr['n'])+u"条记录已被插入" 
        return jsonstr 

    #----------------------------------------------------------------------
    def add_show_like(self, args):
        """"""
        jsonstr = {}
        from_user = args.get('from_user', '')
        to_show_id = int(args.get('to_show_id', -1))
        from_user_id = self.get_user_id(from_user)
        sql = "select * from LikeShows where from_user_id=" + str(from_user_id) + " and to_show_id=" + str(to_show_id) + ";"
        n = self._db.execute(sql)
        if n==1:
            sql = "delete from LikeShows where from_user_id=" + str(from_user_id) + " and to_show_id=" + str(to_show_id) + ";"
            n = self._db.execute(sql)
            if n==1:
                jsonstr['code'] = -1
                jsonstr['msg'] = from_user + u'减赞成功' 
                return jsonstr 
            else: 
                jsonstr['code'] = -2
                jsonstr['msg'] = from_user + u'减赞失败' 
                return jsonstr    
        sql = "insert into LikeShows(from_user_id, to_show_id) values(" + str(from_user_id) + ", " + str(to_show_id) + ");"
        n = self._db.execute(sql)
        if n==1:
            jsonstr['code'] = 1
            jsonstr['msg'] = from_user + u'加赞成功' 
            
            from_user_id = self.get_user_id(from_user)
            to_user_id = self.get_user_id_by_show_id(to_show_id)
            if from_user_id!=to_user_id:
                msg_type = 2
                message = from_user+u" 给你点了个赞！" 
                msg_obj = str(to_show_id)
                self.insert_msg(from_user_id, to_user_id, msg_type, message, msg_obj)    
            
            return jsonstr
        else:  
            jsonstr['code'] = 2
            jsonstr['msg'] = from_user + u'加赞失败'
            return jsonstr

 #----------------------------------------------------------------------
    def del_show(self, username, show_id):
        """"""
        jsonstr = {}
        if not username or not self.exist_username(username):
            jsonstr['code'] = -1
            jsonstr['msg'] = u'用户不存在' 
            return jsonstr
        user_id = self.get_user_id(username)
        show_id = int(show_id)
        sql = "delete from Shows where show_id=" + str(show_id) + " and user_id=" + str(user_id) + ";"
        n = self._db.execute(sql)
        if n==1:
            jsonstr['code'] = 1
            jsonstr['msg'] = u'删除show成功' 
            return jsonstr
        else:  
            jsonstr['code'] = 0
            jsonstr['msg'] = u'删除show失败' 
            return jsonstr

# 评论相关
    #----------------------------------------------------------------------
    def post_show_comment(self, username, to_show_id, content, is_anonymous):
        """"""
        jsonstr = {}
        sql = "insert into Comments(content, is_anonymous) values(%s, "+str(is_anonymous)+");"
        n = self._db.insert_one(sql, [content])
        from_user_id = self.get_user_id(username)
        if n and from_user_id!=-1:
            sql = "insert into CommentShows(from_user_id, to_show_id, comment_id) values("+str(from_user_id)+", "+str(to_show_id)+", "+str(n)+");"
            n = self._db.execute(sql)
            if n==1:
                jsonstr['code'] = 1
                jsonstr['msg'] = u'评论成功'
                     
                from_user_id = from_user_id
                to_user_id = self.get_user_id_by_show_id(to_show_id)
                if from_user_id!=to_user_id:
                    msg_type = 2
                    message = username+u" 给了您一个新评论: "+content
                    msg_obj = str(to_show_id)
                    self.insert_msg(from_user_id, to_user_id, msg_type, message, msg_obj)                 
                 
                return jsonstr
            else:
                jsonstr['code'] = -1
                jsonstr['msg'] = u'评论失败'
                return jsonstr
        else:
            jsonstr['code'] = -2
            jsonstr['msg'] = u'不存在这个用户'
            return jsonstr

    #----------------------------------------------------------------------
    def insert_comment_msg(self, username, from_user_id, comment_id, content):
        """"""
        #logging.error(comment_id) 
        to_show_id = -1
        sql = "select to_show_id from CommentShows where comment_id=" + str(comment_id) + " limit 1;"
        n = self._db.execute(sql) 
        if n==1:
            to_show_id = self._db.fetchOne()[0] 
            to_user_id = self.get_user_id_by_show_id(to_show_id)
            if from_user_id!=to_user_id:
                msg_type = 2 
                message = username+u" 回复了您: "+content
                msg_obj = str(to_show_id)
                self.insert_msg(from_user_id, to_user_id, msg_type, message, msg_obj)  
                return
            
        sql = "select from_user_id, to_comment_id from ReplyComments where comment_id=" + str(comment_id) + " limit 1;" 
        n = self._db.execute(sql)
        if n==1: 
            q = self._db.fetchOne()
            to_user_id = q[0]
            if from_user_id!=to_user_id:
                msg_type = 2
                message = username+u" 回复了您: "+content
                msg_obj = str(to_show_id)
                self.insert_msg(from_user_id, to_user_id, msg_type, message, msg_obj) 
                
            return self.insert_comment_msg(username, from_user_id, q[1], content) 
        return -1 
    
    #----------------------------------------------------------------------
    def get_from_use_ids(self, comment_id, user_ids):
        """"""
        sql = "select to_show_id from CommentShows where comment_id=" + str(comment_id) + " limit 1;"
        n = self._db.execute(sql) 
        if n==1:       
            to_show_id = self._db.fetchOne()[0]
            user_ids.append(self.get_user_id_by_show_id(to_show_id))
            user_ids.append(to_show_id)
            return user_ids
        sql = "select from_user_id, to_comment_id from ReplyComments where comment_id=" + str(comment_id) + " limit 1;" 
        n = self._db.execute(sql)
        if n==1:     
            q = self._db.fetchOne()
            to_user_id = q[0] 
            user_ids.append(to_user_id)
            return self.get_from_use_ids(q[1], user_ids)
    
    #----------------------------------------------------------------------
    def post_comment_comment(self, username, to_comment_id, content, is_anonymous):
        """"""
        jsonstr = {}
        sql = "insert into Comments(content, is_anonymous) values(%s, "+str(is_anonymous)+");"
        n = self._db.insert_one(sql, [content])
        from_user_id = self.get_user_id(username)  
        #logging.error(to_comment_id)
        if n and from_user_id!=-1:
            sql = "insert into ReplyComments(from_user_id, to_comment_id, comment_id) values("+str(from_user_id)+", "+str(to_comment_id)+", "+str(n)+");"
            n = self._db.execute(sql)
            #logging.error(n)
            if n==1:
                jsonstr['code'] = 1
                jsonstr['msg'] = u'评论成功'
                
                user_ids = []
                to_user_ids = self.get_from_use_ids(to_comment_id, user_ids)
                to_show_id = to_user_ids[-1]
                #logging.error(to_show_id)
                for to_user_id in to_user_ids[:-1]:
                    if from_user_id!=to_user_id:
                        msg_type = 2
                        message = username+u" 回复了您: "+content
                        msg_obj = str(to_show_id)
                        self.insert_msg(from_user_id, to_user_id, msg_type, message, msg_obj)                     
                #self.insert_comment_msg(username, from_user_id, to_comment_id, content)
                
                return jsonstr
            else:
                jsonstr['code'] = -1
                jsonstr['msg'] = u'评论失败'
                return jsonstr
        else: 
            jsonstr['code'] = -2
            jsonstr['msg'] = u'不存在这个用户'
            return jsonstr


    #----------------------------------------------------------------------
    def get_all_show_comments(self, username, show_id):
        """"""
        jsonstr = {}
        sql = "select Users.username, Comments.content, Comments.comment_id, Comments.is_anonymous, Comments.comment_time from Users, CommentShows, Comments where Users.user_id=from_user_id and Comments.comment_id=CommentShows.comment_id and to_show_id="+str(show_id)+" order by Comments.comment_time;"
        cs = self._db.query_all(sql)
        if cs==None:
            cs = []
        comments = []
        for c in cs:
            comment = {} 
            comment['comment_user'] = c[0]
            comment['commented_user'] = username
            comment['content'] = c[1]
            comment['comment_id'] = c[2]
            comment['is_anonymous'] = c[3]
            comment['comment_time'] = c[4]
            comments.append(comment)
            replys = self.getReplyComments(comment['comment_user'], comment['comment_id'])
            comments.extend(replys)
        jsonstr['code'] = 1
        jsonstr['msg'] = u'获取评论成功' 
        jsonstr['comments'] = comments          
        return jsonstr
    #----------------------------------------------------------------------
    def getReplyComments(self, username, comment_id):
        """"""
        #logging.error(comment_id)
        jsonstr = {}
        sql = "select Users.username, Comments.content, Comments.comment_id, Comments.is_anonymous, Comments.comment_time from Users, ReplyComments, Comments where Users.user_id=from_user_id and Comments.comment_id=ReplyComments.comment_id and to_comment_id="+str(comment_id)+" order by Comments.comment_time;"
        cs = self._db.query_all(sql)
        if cs==None:
            cs = []
        comments = []
        for c in cs:
            comment = {} 
            comment['comment_user'] = c[0]
            comment['commented_user'] = username
            comment['content'] = c[1]
            comment['comment_id'] = c[2]
            comment['is_anonymous'] = c[3]
            comment['comment_time'] = c[4]
            comments.append(comment)
            replys = self.getReplyComments(comment['comment_user'], comment['comment_id'])
            comments.extend(replys)
        return comments
    
    
# message相关
 
    #----------------------------------------------------------------------
    def insert_msg(self, from_user_id, to_user_id, msg_type, message, msg_obj):
        """"""
        from_user_id = int(from_user_id)
        to_user_id = int(to_user_id) 
        msg_type =  int(msg_type)
        sql = "insert into Messages(from_user_id, to_user_id, msg_type, has_read, message, msg_obj) values("+str(from_user_id)+", "+str(to_user_id)+", "+str(msg_type)+", 0, %s, %s);"    
        n = self._db.execute(sql, [message, msg_obj])
        return n==1

    #----------------------------------------------------------------------
    def set_msg_read(self, username, message_id):
        """"""
        jsonstr = {}
        message_id =  int(message_id)
        sql = "update Messages set has_read=1 where message_id="+str(message_id)+";"    
        n = self._db.execute(sql)
        if n==1:
            jsonstr['code'] = 1
            jsonstr['msg'] = u"消息已设为已读" 
            return jsonstr 
        else:
            jsonstr['code'] = -1
            jsonstr['msg'] = u"消息设为已读失败" 
            return jsonstr  
    
    #----------------------------------------------------------------------
    def del_msg(self, username, message_id):
        """"""
        jsonstr = {}
        message_id =  int(message_id)
        sql = "delete from Messages where message_id="+str(message_id)+";"    
        n = self._db.execute(sql)
        if n==1:
            jsonstr['code'] = 1
            jsonstr['msg'] = u"删除消息成功" 
            return jsonstr 
        else:
            jsonstr['code'] = -1
            jsonstr['msg'] = u"删除消息失败" 
            return jsonstr  
    
    #----------------------------------------------------------------------
    def get_unread_num(self, username):
        """"""
        jsonstr = {}
        if not username or not self.exist_username(username):
            jsonstr['code'] = -1
            jsonstr['msg'] = u'不存在这个用户'
            return jsonstr
        to_user_id = self.get_user_id(username)
        sql = "select count(*) from Messages where has_read=0 and to_user_id="+ str(to_user_id) +";"
        n = self._db.query_one(sql)
        jsonstr['code'] = 1
        jsonstr['n'] = n[0];
        jsonstr['msg'] = str(n)+u"条未读消息" 
        return jsonstr 


    #----------------------------------------------------------------------
    def get_unread_message(self, username):
        """"""
        jsonstr = {}
        if not username or not self.exist_username(username):
            jsonstr['code'] = -1
            jsonstr['msg'] = u'不存在这个用户'
            return jsonstr
        to_user_id = self.get_user_id(username)
        sql = "select message_id, from_user_id, to_user_id, msg_type, has_read, message, msg_obj, send_time from Messages where has_read=0 and to_user_id="+ str(to_user_id) +" order by send_time desc;"
        qmsgs = self._db.query_all(sql)
        if qmsgs==None:
            qmsgs = []
        names = ['message_id', 'from_user_id', 'to_user_id', 'msg_type', 'has_read', 'message', 'msg_obj', 'send_time']
        length = len(qmsgs)
        msgs = []
        for qmsg in qmsgs: 
            msg = {}
            for i, name in enumerate(names):
                msg[name] = qmsg[i]
            msg['from_user'] = self.get_username_by_id(msg['from_user_id'])
            msg['to_user'] = self.get_username_by_id(msg['to_user_id'])
            msgs.append(msg)
        jsonstr['msgs'] = msgs
        jsonstr['code'] = 1
        jsonstr['n'] = length;
        jsonstr['msg'] = str(length)+u"条未读消息已被查询" 
        return jsonstr 
            

    #----------------------------------------------------------------------
    def get_read_message(self, username, last_id, num):
        """"""
        jsonstr = {}
        if not username or not self.exist_username(username):
            jsonstr['code'] = -1
            jsonstr['msg'] = u'不存在这个用户'
            return jsonstr
        last_id = int(last_id)
        num = int(num)
        to_user_id = self.get_user_id(username)
        sql = "select message_id, from_user_id, to_user_id, msg_type, has_read, message, msg_obj, send_time from Messages where has_read=1 and message_id<" + str(last_id) + " and to_user_id="+ str(to_user_id) +" order by send_time desc;"
        qmsgs = self._db.query_all(sql)
        if qmsgs==None:
            qmsgs = []
        names = ['message_id', 'from_user_id', 'to_user_id', 'msg_type', 'has_read', 'message', 'msg_obj', 'send_time']
        length = len(qmsgs)
        msgs = []
        for qmsg in qmsgs: 
            msg = {}
            for i, name in enumerate(names):
                msg[name] = qmsg[i]
            msg['from_user'] = self.get_username_by_id(msg['from_user_id'])
            msg['to_user'] = self.get_username_by_id(msg['to_user_id'])
            #logging.error(msg['msg_obj'])
            msgs.append(msg)
        jsonstr['msgs'] = msgs
        jsonstr['code'] = 1
        jsonstr['n'] = length;
        jsonstr['msg'] = str(length)+u"条已读消息已被查询" 
        return jsonstr 
            
    #----------------------------------------------------------------------
    def submit_bug(self, username, bug):
        """"""
        with open('bug.txt', 'a') as f: 
            f.write('\n'+username.encode('utf8')+": \n")  
            #logging.error(bug)
            f.write(bug.encode('utf8')) 
        
        jsonstr = {}
        jsonstr['code'] = 1
        jsonstr['msg'] = u'感谢你的提交，这个应用因为有你会变得更好！'
        return jsonstr
            