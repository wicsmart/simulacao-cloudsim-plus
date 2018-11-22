from elasticsearch import Elasticsearch, helpers
from datetime import datetime, timedelta
import random
import json
import time
import logging
import sys, os

INDEX_NAME, DOC_TYPE = 'bigdata', '_doc'

def store_record(elastic_object, record):
    try:
        print elastic_object.index(
            index=INDEX_NAME, doc_type=DOC_TYPE, body=record)
    except Exception as ex:
        print('Error in indexing data')
        print(str(ex.message))


def store_bulk(elastic_object, bulk):
    try:
        print helpers.bulk(elastic_object, bulk, True)
    except Exception as ex:
        print('Error in indexing data')
        # print(str(ex))


def connect_elasticsearch(endereco, porta):
    _es = None
    _es = Elasticsearch([{'host': endereco, 'port': porta}])
    if _es.ping():
        print 'Elastisearch Connect'
    else:
        print 'Elastisearch is not connect!'
    return _es

def load_files_from_dir(directory):
    for filename in os.listdir(directory):
        print(filename)
        if filename.endswith('.json'):
            filepath = directory + filename
            myfile = open(filepath, 'r').read()
            print(store_bulk(es, json.loads(myfile)))

def load_file(filepath):
    if filepath.endswith('.json'):
        myfile = open(filepath, 'r').read()
        print(store_bulk(es, json.loads(myfile)))

if __name__ == '__main__':
    logging.basicConfig(level=logging.ERROR)
    es = connect_elasticsearch("127.0.0.1","9200")

    for i in range(1,51):
        print 'file'+str(i)
        load_file("/home/wictor/resultado/core"+str(i) + ".json")

   