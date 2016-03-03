#coding:utf-8

from flask import Flask
from funny.MyConfig import MyConfig
from funny.MyViews import app


if __name__ == '__main__':
    address = MyConfig.SERVER_ADDRESS; 
    app.run(host=address['host'], port=address['port'], debug=False, threaded=True)  