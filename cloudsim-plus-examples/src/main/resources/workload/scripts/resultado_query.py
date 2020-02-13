qos_total = "doc['execTimeCore'].value +  doc['execTimeColetor'].value > 2"
qos_core = "doc['execTimeCore'].value > 1"
qos_coletor = "doc['execTimeColetor'].value * 1.68 > 1"

resultado = {
  "size": 0,
  "query": {
    "bool": {
      "filter": [
        {
          "range": {
            "startTimeColetor": {
              "format": "dd-MM-yyyy HH:mm:ss",
              "gte": "07-11-2018 10:00:00",
              "lte": "07-11-2018 22:00:00"
            }
          }
        }
      ]
    }
  },
  "aggs": {
    "confiInicial": {
      "filter": {
        "term": {
          "nome.keyword": "confiInicial"
        }
      },
      "aggs": {
        "quantidade": {
          "filter": {
            "script": {
              "script": {
                "source": qos_total
              }
            }
          }
        },
          "core": {
          "filter": {
            "script": {
              "script": {
                "source": qos_core
              }
            }
          }
        },
          "coletor": {
          "filter": {
            "script": {
              "script": {
                "source": qos_coletor
              }
            }
          }
        }
      }
    },
     "confIntermediaria": {
      "filter": {
        "term": {
          "nome.keyword": "confIntermediaria"
        }
      },
      "aggs": {
        "quantidade": {
          "filter": {
            "script": {
              "script": {
                "source": qos_total
              }
            }
          }
        },
          "core": {
          "filter": {
            "script": {
              "script": {
                "source": qos_core
              }
            }
          }
        },
          "coletor": {
          "filter": {
            "script": {
              "script": {
                "source": qos_coletor
              }
            }
          }
        }
      }
    },
     "confOtimizada": {
      "filter": {
        "term": {
          "nome.keyword": "confOtimizada"
        }
      },
      "aggs": {
        "quantidade": {
          "filter": {
            "script": {
              "script": {
                "source": qos_total
              }
            }
          }
        },
          "core": {
          "filter": {
            "script": {
              "script": {
                "source": qos_core
              }
            }
          }
        },
          "coletor": {
          "filter": {
            "script": {
              "script": {
                "source": qos_coletor
              }
            }
          }
        }
      }
    }
  }
}
