from gensim.models import KeyedVectors

# GoogleNews vector model
model = KeyedVectors.load_word2vec_format('GoogleNews-vectors-negative300.bin', binary=True)

# limit n load
top_n = 50000 
limited_model = KeyedVectors(vector_size=model.vector_size)

# n개의 공간 확보
limited_model.add_vectors([model.index_to_key[i] for i in range(top_n)], 
                          [model[model.index_to_key[i]] for i in range(top_n)])

# remodeling...
limited_model.save_word2vec_format('GoogleNews-limited-50000.bin', binary=True)

print("새로운 모델 파일이 저장되었습니다: GoogleNews-limited-50000.bin")
