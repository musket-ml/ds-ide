import os
from musket_core import inference
from flask import Flask,jsonify
app = Flask(__name__)
from flask import request

#Let's create inference engine
engine=inference.create_engine(os.path.join(os.path.dirname(__file__),"../assets/"),multi_threaded=True)


@app.route('/', methods=['POST'])
def do_infer():
    data=request.json         
    return jsonify(engine(data)), 201
 
if __name__ == '__main__':
    app.run(debug=True)