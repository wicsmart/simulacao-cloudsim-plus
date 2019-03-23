import pandas as pd
from pandas.io.json import json_normalize
import json
import sys, os
os.chdir(os.path.dirname(__file__))
_path = '/home/wictor/simulacao/cloudsim-plus-examples/src/main/resources/workload/swf/'

class ParseLog:

    def __init__(self):
        self.frame = None
     

    def write(self, file_name):
        copy = self.frame.drop(['doc_count','key_as_string'], axis=1)
        copy.to_csv(_path+file_name, sep=' ', header=False)
        print 'Log salvo em ' + _path+file_name

    def parse(self, logs):
        self.frame = pd.DataFrame.from_dict(json_normalize(logs), orient='columns')
        copy = self.frame.drop(['doc_count','key_as_string'], axis=1)

    def read(self, path):
        with open(path) as f:
            dados = json.load(f)
        self.frame = pd.DataFrame.from_dict(json_normalize(dados), orient='columns')
        return self.frame

    def estatistica(self):
        print self.frame.describe()

    def show(self):
        print self.frame  
    
    def somaColuna(self):
        print self.frame.sum()

    def multiplica(self, f):
       self.frame['pontos.value'] = self.frame['pontos.value'].apply(lambda x:x*f)
       
