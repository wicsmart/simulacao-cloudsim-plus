
import logging
import json
import pandas as pd
from pandas.io.json import json_normalize
from elastic import Elastic
from querys import resultado as res

if __name__ == '__main__':

    pd.set_option('display.max_columns', None)  
    pd.set_option('display.expand_frame_repr', False)
    pd.set_option('max_colwidth', -1)

    logging.basicConfig(level=logging.ERROR)
    elastic = Elastic("127.0.0.1","9200")
    elastic.connect()
    
    result = elastic.searchResult('bigdata', '_doc', res)
    '''Convert dic to frame'''
    resultado = [
        {
       'vms' : 'c42x-c42xc44x',
       'total_msg' : result['c42x-c42xc44xsim']['doc_count'],
       'msg_violadas' : result['c42x-c42xc44xsim']['quantidade']['doc_count'],
       'core_violadas' : result['c42x-c42xc44xsim']['core']['doc_count'],
       'coletor_violadas' : result['c42x-c42xc44xsim']['coletor']['doc_count'],
       'custo' : 5
      },
        {
       'vms' : 'c42xc42x',
       'total_msg' : result['c42xc42x']['doc_count'],
       'msg_violadas' : result['c42xc42x']['quantidade']['doc_count'],
         'core_violadas' : result['c42xc42x']['core']['doc_count'],
       'coletor_violadas' : result['c42xc42x']['coletor']['doc_count'],
       'custo' : 4
      },
        {
       'vms' : 'c42xc4x-c42xc44x',
       'total_msg' : result['c42xc4x-c42xc44xsim']['doc_count'],
       'msg_violadas' : result['c42xc4x-c42xc44xsim']['quantidade']['doc_count'],
           'core_violadas' : result['c42xc4x-c42xc44xsim']['core']['doc_count'],
       'coletor_violadas' : result['c42xc4x-c42xc44xsim']['coletor']['doc_count'],
       'custo' : 4.5
      },
      {
       'vms' : 'c4x-c42xhorz',
       'total_msg' : result['c4x-c42xhorz']['doc_count'],
       'msg_violadas' : result['c4x-c42xhorz']['quantidade']['doc_count'],
       'core_violadas' : result['c4x-c42xhorz']['core']['doc_count'],
       'coletor_violadas' : result['c4x-c42xhorz']['coletor']['doc_count'],
       'custo' : 4.5
      }
    ]
    colunas = ['vms', 'total_msg', 'msg_violadas', 'custo', 'custo_adicional(%)',
                 'qos_coletor', 'qos_core',"qos_total", 'ganho_qos(%)']
    frame = pd.DataFrame.from_dict(json_normalize(resultado))

    '''Calcula metricas'''

    frame['qos_total'] = 1 - (frame.msg_violadas / frame.total_msg)
    frame['qos_core'] = 1 - (frame.core_violadas / frame.total_msg)
    frame['qos_coletor'] = 1 - (frame.coletor_violadas / frame.total_msg)
    frame['custo_adicional(%)'] = ((frame.custo / 4.0) -1 ) * 100 
    frame['ganho_qos(%)'] = (1-(frame.msg_violadas /frame.at[1,'msg_violadas']) ) * 100
    frame = frame.reindex(columns=colunas)
    frame = frame.sort_values(by=['qos_total'], ascending = [0])

  
    print frame.round({'qos_total' : 4, 'qos_coletor':4, 'ganho_qos(%)': 4, 'qos_core':4})
 #   print json.dumps(result, indent=2, sort_keys=True)

