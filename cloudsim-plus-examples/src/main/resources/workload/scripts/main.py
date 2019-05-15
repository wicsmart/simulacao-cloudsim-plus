
import logging
import sys, os
from elastic import Elastic
from parselog import ParseLog

query = {
  "size": 0,
  "query": {
    "bool": {
      "filter": [
        {
          "range": {
            "timestamp": {
              "format": "dd-MM-yyyy HH:mm:ss",
              "gte": "07-11-2018 18:00:01",
              "lte": "07-11-2018 20:00:00"
            }
          }
        }
      ]
    }
  },
  "aggs": {
    "serie": {
      "date_histogram": {
        "field": "timestamp",
        "interval": "1s"
      },
      "aggs": {
        "pontos": {
          "sum": {
            "field": "pontos"
          }
        }
      }
    }
  }
}
if __name__ == '__main__':
    logging.basicConfig(level=logging.ERROR)
    elastic = Elastic("127.0.0.1","9200")
    elastic.connect()
    result = elastic.searchAnalyse('graylog*', 'message', query)
    logs = ParseLog()
    logs.parse(result)
    logs.multiplica(0.1)
    logs.somaColuna()
    logs.show()  
    logs.estatistica()  

    logs.write('um18.swf')
