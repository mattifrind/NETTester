import pickle
with open('detection_range_0_5000.pkl', 'rb') as f:
    data = pickle.load(f)
    newFile = open("unpickled.txt", "w")
    newFile.write(str(data))
    newFile.flush()
    newFile.close()