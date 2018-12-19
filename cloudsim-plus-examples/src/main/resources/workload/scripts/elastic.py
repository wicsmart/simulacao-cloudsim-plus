from elasticsearch import Elasticsearch, helpers
import json

class Elastic:

    def __init__(self, endereco, porta):
        self.endereco = endereco
        self.porta = porta
        self.es = None

    def connect(self):
        self.es = Elasticsearch([{'host': self.endereco, 'port': self.porta}])
        if self.es.ping():
            print 'Elastisearch Connect'
        else:
            print 'Elastisearch isn`t connect!'
        return self.es
    
    def searchAllDoc(self, index, doc_type, query):
        results = helpers.scan(self.es, index=index, doc_type=doc_type, query=query)
        dict_res = []
        for item in results:
            dict_res.append(item)
        return dict_res

    def searchAnalyse(self, index, doc_type, query):
        res =  self.es.search(index='graylog*', doc_type='message', body=query)
        print("%d documents found" % res['hits']['total'])
        return res['aggregations']['serie']['buckets']
        