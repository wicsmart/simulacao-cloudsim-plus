from elasticsearch import Elasticsearch, helpers
from datetime import datetime, timedelta
import json
import time
import logging
import sys, os

INDEX_NAME, DOC_TYPE = 'bigdata', '_doc'

def connect_elasticsearch(endereco, porta):
    _es = None
    _es = Elasticsearch([{'host': endereco, 'port': porta}])
    if _es.ping():
        print 'Elastisearch Connect'
    else:
        print 'Elastisearch is not connect!'
    return _es

if __name__ == '__main__':
    logging.basicConfig(level=logging.ERROR)
    es = connect_elasticsearch("127.0.0.1","9200")
    query = {
        "size": 10,
        "query": {
            "bool": {
            "filter": [
                {
                "term": {
                    "nome.keyword": "vms22Sim"
                }
                }
            ]
            }
        }
    }
    results = helpers.scan(es, index=INDEX_NAME, doc_type=DOC_TYPE, query=query)

    dict_res = []

    for item in results:
        dict_res.append(item)
    
    with open('/home/wictor/resultado/testeEscrita.json', 'w') as fp:
        json.loads(dict_res, fp)
