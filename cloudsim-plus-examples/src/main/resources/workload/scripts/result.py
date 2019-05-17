
import logging
import pandas as pd
from pandas.io.json import json_normalize
from elastic import Elastic
from querys import resultado as res

if __name__ == '__main__':
    logging.basicConfig(level=logging.ERROR)
    elastic = Elastic("127.0.0.1","9200")
    elastic.connect()
    
    result = elastic.searchResult('bigdata', '_doc', res)

    resultado = [
       {
       'vms' : 'c4xc44x',
       'total_msg' : result['c4xc44x']['doc_count'],
       'msg_violadas' : result['c4xc44x']['quantidade']['doc_count'],
       'custo' : 5
      },
        {
       'vms' : 'c42x-c42xc44x',
       'total_msg' : result['c42x-c42xc44xsim']['doc_count'],
       'msg_violadas' : result['c42x-c42xc44xsim']['quantidade']['doc_count'],
       'custo' : 5
      },
        {
       'vms' : 'c42xc42x',
       'total_msg' : result['c42xc42x']['doc_count'],
       'msg_violadas' : result['c42xc42x']['quantidade']['doc_count'],
       'custo' : 4
      },
        {
       'vms' : 'c42xc4x-c42xc44x',
       'total_msg' : result['c42xc4x-c42xc44xsim']['doc_count'],
       'msg_violadas' : result['c42xc4x-c42xc44xsim']['quantidade']['doc_count'],
       'custo' : 4.5
      }
    ]
    colunas = ['vms', 'total_msg', 'msg_violadas', 'custo',"qos%"]
    frame = pd.DataFrame.from_dict(json_normalize(resultado))

    frame['qos%'] = (frame.msg_violadas / frame.total_msg)* 100
    
    frame = frame.reindex(columns=colunas)
    frame = frame.sort(['qos%'], ascending = [1])
    print frame.round({'qos%' : 2})

