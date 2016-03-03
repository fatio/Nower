#coding:utf-8

import os

########################################################################
class MyConfig:
    """配置类"""

    #本机服务器地址及端口
    SERVER_ADDRESS = {'host':'0.0.0.0', 'port':8080}
    
    #数据库连接参数  
    DB_CONFIG = {'host':'localhost', 
                'port': 3306, 
                'user':'root', 
                'passwd':'toor', 
                'db':'Playing', 
                'charset':'utf8'}    
    
    #文件路径
    IMAGE_DIR = os.path.join(os.path.split(os.path.realpath(__file__))[0], '../files/images/')
    AUDIO_DIR = os.path.join(os.path.split(os.path.realpath(__file__))[0], '../files/audios/')    

    #允许的扩展名
    ALLOWED_EXTENSIONS  = set(['mp3', 'wav', 'wma', 'amr', 'ogg', 'ape', 'acc', 'mp4', 'avi', 'png', 'jpg', 'jpeg', 'gif'])
    
    #token有效期 
    TOKEN_TIME_OUT = 1000 * 3600 #1000分钟

        
        
    
    