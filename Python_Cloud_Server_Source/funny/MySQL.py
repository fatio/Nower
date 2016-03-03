#coding:utf-8

import MySQLdb
import time
import logging

########################################################################
class MySQL:
    """封装MySQL常用操作"""
    
    _conn = None #数据库连接
    _cur = None #数据库游标
    _dbConfig = None 
    #----------------------------------------------------------------------
    def __init__(self, dbConfig):
        """构造连接"""
        self._dbConfig = dbConfig 
        
        self.connectMySQL()
        
        self._cur = self._conn.cursor()
    
    #----------------------------------------------------------------------
    def __del__(self):
        """自动释放资源"""
        try:
            self._cur.close()
            self._conn.close()
        except:
            pass
    #----------------------------------------------------------------------
    def connectMySQL(self):
        """"""
        try:
            self._conn = MySQLdb.connect(host = self._dbConfig['host'],
                                     port = self._dbConfig['port'],
                                     user = self._dbConfig['user'],
                                     passwd = self._dbConfig['passwd'],
                                     db = self._dbConfig['db'],
                                     charset = self._dbConfig['charset'])
            return True
        except MySQLdb.Error, e:
            print 'MySQL connect error!', e.args[0], e.args[1]
            return False
            
    #----------------------------------------------------------------------
    def persistentConnect(self):
        """"""
        try:
            self._conn.ping()
        except Exception, e:
            logging.error(u'mysql断了')
            print u'mysql断了'
            logging.error(str(e))
            while True:
                if self.connectMySQL():
                    self._cur = self._conn.cursor()
                    break
                logging.error(u'mysql重连失败')
                print u'mysql重连失败'
                time.sleep(2)
        
    #----------------------------------------------------------------------
    def close(self):
        """关闭数据库连接"""
        self.__del__()

    #----------------------------------------------------------------------
    def execute(self, sql, args=None):
        """执行SQL语句并返回影响行数"""
        try:
            self.persistentConnect()
            self._cur.execute("SET NAMES utf8") 
            result = self._cur.execute(sql, args)
            self._conn.commit()
        except MySQLdb.Error, e:
            self._conn.rollback()
            result = False
            print 'MySQL execute error!', e.args[0], e.args[1]
        return result

    #----------------------------------------------------------------------
    def executemany(self, sql, args=None):
        """执行SQL语句集"""
        try:
            self.persistentConnect()
            self._cur.execute("SET NAMES utf8") 
            result = self._cur.executemany(sql, args)
            self._conn.commit()
        except MySQLdb.Error, e:
            self._conn.rollback()
            result = False
            print 'MySQL executemany error!', e.args[0], e.args[1]
        return result     
    
    #----------------------------------------------------------------------
    def rowcount(self):
        """获取返回的结果行数"""
        return self._cur.rowcount
    #----------------------------------------------------------------------
    def commit(self):
        """提交操作"""
        self._conn.commit()
        #----------------------------------------------------------------------
    def rollback(self):
        """回滚操作"""
        self._conn.rollback()
    #----------------------------------------------------------------------
    def fetchOne(self):
        """获取一行记录，一维元祖"""
        return self._cur.fetchone()
    #----------------------------------------------------------------------
    def fetchMany(self, n):
        """获取n条记录，二维元祖"""
        return self._cur.fetchmany(n)
        
    #----------------------------------------------------------------------
    def fetchAll(self):
        """获取所有结果记录，二维元祖"""
        return self._cur.fetchall()
    #----------------------------------------------------------------------
    def cursor(self):
        """获取数据库游标"""
        return self._cur
    #----------------------------------------------------------------------
    def connection(self):
        """获取数据库连接"""
        self.persistentConnect()
        return self._conn
    
    
    #----------------------------------------------------------------------
    def query_one(self, sql, args=None):
        """查询一条记录，返回列表"""
        if self.execute(sql, args):
            return list(self.fetchOne())
    #----------------------------------------------------------------------
    def query_many(self, sql, n, args=None):
        """查询n条记录，返回列表，内为元祖"""
        if self.execute(sql, args):
            res = list(self.fetchMany(n)) 
            return res    
    #----------------------------------------------------------------------
    def query_all(self, sql, args=None):
        """查询所有记录，返回列表，内为元祖"""
        if self.execute(sql, args):
            res = list(self.fetchAll())
            return res
    
    #----------------------------------------------------------------------
    def insert_one(self, sql, args=None):
        """执行SQL插入语句并返回自增ID"""
        try:
            self.persistentConnect()
            self._cur.execute("SET NAMES utf8") 
            result = self._cur.execute(sql, args)
            result = self._conn.insert_id();
            self._conn.commit()
        except MySQLdb.Error, e:
            self._conn.rollback()
            result = False
            print 'MySQL insert_one error!', e.args[0], e.args[1]
        return result
    
if __name__ == '__main__':
    from MyConfig import MyConfig
    db = MySQL(MyConfig.DB_CONFIG)    
    sql = "select * from users;"
    print db.query_all(sql)


    pass 