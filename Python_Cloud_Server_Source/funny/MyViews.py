#coding:utf-8

from flask import Flask, jsonify, send_file, request, url_for, redirect
from werkzeug import secure_filename
import logging 
import os
import hashlib
import json
from urllib import unquote

from MyModel import MyModel
from MyConfig import MyConfig



app = Flask(__name__)
#md = MyModel()


# 位置相关*****************************************************************************
@app.route('/upload_location/', methods=["GET", "POST"]) 
def upload_location():
    """
    上传位置 POST /upload_location/
    address=str & latitude=str & longitude=str & description=str
    """
    md = MyModel()
    address = request.form.get('address', request.args.get('address', ''))
    latitude = request.form.get('latitude', request.args.get('latitude', ''))
    longitude = request.form.get('longitude', request.args.get('longitude', ''))
    description = request.form.get('description', request.args.get('description', ''))
    jsonstr = md.upload_location(address, latitude, longitude, description)
    return jsonify(jsonstr)

# 文件相关********************************************************************************
@app.route('/get_head_pic/', methods=["GET"])
def get_head_pic():
    """
    获取用户头像 GET /get_head_pic/
    username=str
    """
    md = MyModel()
    username = request.form.get('username', request.args.get('username', ''))
    imgname = md.get_user_head_pic(username)
    return send_file(os.path.join(MyConfig.IMAGE_DIR, imgname), mimetype='image')

@app.route('/get_image/<fileName>', methods=["GET"])
def get_image(fileName):
    """
    下载图片 GET /get_image/<imgname>
    """
    return send_file(os.path.join(MyConfig.IMAGE_DIR, fileName), mimetype='image')

@app.route('/get_audio/<fileName>', methods=["GET"])
def get_audio(fileName):
    return send_file(os.path.join(MyConfig.AUDIO_DIR, fileName), mimetype='audio') 

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1] in MyConfig.ALLOWED_EXTENSIONS

def get_file_hash_name(filecontent, filename):
    """hash文件名"""
    return ''.join([hashlib.md5(filecontent).hexdigest(), '.', filename.rsplit('.', 1)[1]])

@app.route('/upload_image/', methods=["GET", "POST"])
def upload_image():
    md = MyModel()
    jsonstr = {}
    if request.method == 'POST':
        uploaded_files = request.files.getlist('file[]')
        #hfile = request.files['file'] 
        filenames = []
        for hfile in uploaded_files:
            if hfile and allowed_file(hfile.filename):
                filename = secure_filename(hfile.filename)
                filecontent = hfile.read()
                hfile.seek(0) 
                filename = get_file_hash_name(filecontent, filename)
                hfile.save(os.path.join(MyConfig.IMAGE_DIR, filename))
                filenames.append(filename)
        jsonstr['filenames'] = '|'.join(filenames)  
        jsonstr['code'] = 1
        jsonstr['msg'] = str(len(uploaded_files)) + u'张图片上传成功'    
        return jsonify(jsonstr) 
    else:
        return '''
            <!doctype html>
            <title>Upload new File</title>
            <h1>Upload new File</h1>
            <form action="" method=post enctype=multipart/form-data>
              <p><input type=file name="file[]">
                 <input type=submit value=Upload>
            </form>
            '''    
@app.route('/upload_audio/', methods=["GET", "POST"])
def upload_audio():
    md = MyModel()
    jsonstr = {}
    if request.method == 'POST':
        hfile = request.files.get('file')
        if hfile and allowed_file(hfile.filename):
            filename = secure_filename(hfile.filename)
            filecontent = hfile.read()
            hfile.seek(0) 
            filename = get_file_hash_name(filecontent, filename)
            hfile.save(os.path.join(MyConfig.AUDIO_DIR, filename))
            jsonstr['filename'] = filename 
            jsonstr['code'] = 1
            jsonstr['msg'] = u'1个文件上传成功'    
            return jsonify(jsonstr) 
    else:
        return '''
            <!doctype html>
            <title>Upload new File</title>
            <h1>Upload new File</h1>
            <form action="" method=post enctype=multipart/form-data>
              <p><input type=file name="file">
                 <input type=submit value=Upload>
            </form>
            '''    
    
    
# 用户相关 **********************************************************************
@app.route('/register/', methods=["GET", "POST"])
def register():
    """
    注册 POST /register/
    username=str & password=str
    """
    md = MyModel()
    username = request.form.get('username', request.args.get('username', ''))
    password = request.form.get('password', request.args.get('password', ''))
    phone = request.form.get('phone', request.args.get('phone', ''))
    head_pic = request.form.get('head_pic', request.args.get('head_pic', ''))
    jsonstr = md.register(username, password, phone, head_pic)  
    return jsonify(jsonstr) 
    
@app.route('/login/', methods=["GET", "POST"])
def login():
    """
    登陆 POST /login/
    username=str & password=str
    """
    md = MyModel()
    username = request.form.get('username', request.args.get('username', ''))
    password = request.form.get('password', request.args.get('password', ''))
    jsonstr = md.login(username, password)
    return jsonify(jsonstr) 

@app.route('/update_password/', methods=["GET", "POST"])
def update_password():
    """
    登陆 POST /login/
    username=str & phone=str & password=str
    """
    md = MyModel()
    username = request.form.get('username', request.args.get('username', ''))
    phone = request.form.get('phone', request.args.get('phone', ''))
    password = request.form.get('password', request.args.get('password', ''))
    jsonstr = md.update_password(username, phone, password)
    return jsonify(jsonstr) 

@app.route('/update_mood/', methods=["GET", "POST"])
def update_mood():
    """
    登陆 POST /login/
    username=str & mood=str
    """
    md = MyModel()
    username = request.form.get('username', request.args.get('username', ''))
    mood = request.form.get('mood', request.args.get('mood', '这家伙目测心情不好，我们来安慰安慰ta吧.'))
    jsonstr = md.update_mood(username, mood)
    return jsonify(jsonstr)

@app.route('/update_user_info/', methods=["GET", "POST"])
def update_user_info():
    """
    登陆 POST /login/
    username=str & phone=str & head_pic=str & mood=str & sex=str & age=str & state=str
    """
    md = MyModel()
    username = request.form.get('username', request.args.get('username', ''))
    phone = request.form.get('phone', request.args.get('phone', ''))
    head_pic = request.form.get('head_pic', request.args.get('head_pic', 'default_head.png'))
    mood = request.form.get('mood', request.args.get('mood', ''))
    sex = request.form.get('sex', request.args.get('sex', '3'))
    age = request.form.get('age', request.args.get('age', '18'))
    state = request.form.get('state', request.args.get('state', '1'))
    jsonstr = md.update_user_info(username, phone, mood, head_pic, sex, age, state)
    return jsonify(jsonstr) 


@app.route('/get_user_info/', methods=["GET", "POST"]) 
def get_user_info():
    md = MyModel()
    username = request.form.get('username', request.args.get('username', ''))
    jsonstr = md.get_user_info(username) 
    return jsonify(jsonstr)

# shows相关
@app.route('/get_my_shows/', methods=["GET", "POST"])
def get_my_shows():
    """
    token=str & username=str & show_id=int & start=int & num=int
    """
    md = MyModel()
    token = request.form.get('token', request.args.get('token', ''))
    username = request.form.get('username', request.args.get('username', ''))
    show_id = request.form.get('show_id', request.args.get('show_id', 10000000000))
    start = request.form.get('start', request.args.get('start', 0))
    num = request.form.get('num', request.args.get('num', 0))
    jsonstr = md.get_my_shows(username, show_id, start, num) 
    return jsonify(jsonstr)

@app.route('/get_neighbor_shows/', methods=['GET', "POST"])
def get_neighbor_shows():
    """
    token=str & address=str & show_id=int & start=int & num=int
    """
    md = MyModel()
    token = request.form.get('token', request.args.get('token', ''))
    username = request.form.get('username', request.args.get('username', ''))
    address = request.form.get('address', request.args.get('address', ""))
    show_id = request.form.get('show_id', request.args.get('show_id', 10000000000))
    start = request.form.get('start', request.args.get('start', 0))
    num = request.form.get('num', request.args.get('num', 0))
    jsonstr = md.get_neighbor_shows(address, username, show_id, start, num)
    return jsonify(jsonstr)

@app.route('/get_one_show/', methods=['GET', "POST"])
def get_one_show():
    """
    token=str & username=str & show_id=int
    """
    md = MyModel()
    token = request.form.get('token', request.args.get('token', ''))
    username = request.form.get('username', request.args.get('username', ''))
    show_id = request.form.get('show_id', request.args.get('show_id', 10000000000))
    jsonstr = md.get_one_show(username, show_id)
    return jsonify(jsonstr)

@app.route('/post_show/', methods=['GET', 'POST'])
def post_show():
    """
    token=str & username=str & content=str & images=str & audios=str & is_anonymous=int & address=str
    """
    md = MyModel()
    jsonstr = md.upload_show(request.form)
    return jsonify(jsonstr)   
 
@app.route('/add_show_like/', methods=['GET', 'POST'])
def add_show_like():
    """
    token=str & from_user=str & & to_show_id
    """
    md = MyModel()
    jsonstr = md.add_show_like(request.form)
    return jsonify(jsonstr)   

@app.route('/del_show/', methods=['GET', 'POST'])
def del_show():
    """
    token=str & username=str & show_id=str
    """
    md = MyModel()
    username = request.form.get('username', request.args.get('username', ''))
    show_id = request.form.get('show_id', request.args.get('show_id', -1))
    jsonstr = md.del_show(username, show_id)
    return jsonify(jsonstr)  

# 评论相关
@app.route('/post_show_comment/', methods=['GET', 'POST'])
def post_show_comment():
    """
    token=str & username=str & to_show_id=int & content=str & is_anonymous=int
    """
    md = MyModel()
    username = request.form.get('username', request.args.get('username', ''))
    to_show_id = request.form.get('to_show_id', request.args.get('to_show_id', -1)) 
    content = request.form.get('content', request.args.get('content', ''))   
    is_anonymous = request.form.get('is_anonymous', request.args.get('is_anonymous', 0))   
    jsonstr = md.post_show_comment(username, to_show_id, content, is_anonymous)
    return jsonify(jsonstr)

@app.route('/post_comment_comment/', methods=['GET', 'POST'])
def post_comment_comment():
    """
    token=str & username=str & to_comment_id=int & content=str & is_anonymous=int
    """
    md = MyModel()
    username = request.form.get('username', request.args.get('username', ''))
    to_comment_id = request.form.get('to_comment_id', request.args.get('to_comment_id', -1)) 
    content = request.form.get('content', request.args.get('content', ''))   
    is_anonymous = request.form.get('is_anonymous', request.args.get('is_anonymous', 0))   
    jsonstr = md.post_comment_comment(username, to_comment_id, content, is_anonymous)
    return jsonify(jsonstr)
 

@app.route('/get_all_show_comments/', methods=['GET', 'POST'])
def get_all_show_comments():
    """
    token=str & username=str & show_id=str
    """
    md = MyModel()
    username = request.form.get('username', request.args.get('username', ''))
    show_id = request.form.get('show_id', request.args.get('show_id', -1))
    jsonstr = md.get_all_show_comments(username, show_id)
    return jsonify(jsonstr)
 
 
 # message相关
 
@app.route('/get_unread_num/', methods=['GET', 'POST'])
def get_unread_num():
    """
    token=str & username=str
    """
    md = MyModel()
    username = request.form.get('username', request.args.get('username', ''))
    jsonstr = md.get_unread_num(username)
    return jsonify(jsonstr) 
 
@app.route('/set_msg_read/', methods=['GET', 'POST'])
def set_msg_read():
    """
    token=str & message_id=str
    """
    md = MyModel()
    username = request.form.get('username', request.args.get('username', ''))
    message_id = request.form.get('message_id', request.args.get('message_id', ''))
    jsonstr = md.set_msg_read(username, message_id)
    return jsonify(jsonstr) 
 
@app.route('/del_msg/', methods=['GET', 'POST'])
def del_msg():
    """
    token=str & message_id=str
    """
    md = MyModel()
    username = request.form.get('username', request.args.get('username', ''))
    message_id = request.form.get('message_id', request.args.get('message_id', ''))
    jsonstr = md.del_msg(username, message_id)
    return jsonify(jsonstr) 

@app.route('/get_unread_message/', methods=['GET', 'POST'])
def get_unread_message():
    """
    token=str & username=str
    """
    md = MyModel()
    username = request.form.get('username', request.args.get('username', ''))
    jsonstr = md.get_unread_message(username)
    return jsonify(jsonstr) 
 
@app.route('/get_read_message/', methods=['GET', 'POST'])
def get_read_message():
    """
    token=str & username=str & last_id=str & num=str
    """
    md = MyModel()
    username = request.form.get('username', request.args.get('username', ''))
    last_id = request.form.get('last_id', request.args.get('last_id', '0'))
    num = request.form.get('num', request.args.get('num', '8'))
    jsonstr = md.get_read_message(username, last_id, num)
    return jsonify(jsonstr) 

@app.route('/submit_bug/', methods=['GET', 'POST'])
def submit_bug():
    """
    token=str & username=str & bug=str
    """
    md = MyModel()
    username = request.form.get('username', request.args.get('username', ''))
    bug = request.form.get('bug', request.args.get('bug', ''))
    jsonstr = md.submit_bug(username, bug)
    return jsonify(jsonstr) 

@app.route('/')
def main(): 
    return 'f'

if __name__ == '__main__':
    
    pass