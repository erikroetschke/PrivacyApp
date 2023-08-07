import geoDetection.stop_detection
import pandas as pd

def extract_pois(route, time_threshold, distance_threshold):
    res_list = []
    pois = (geoDetection.stop_detection.extract_pois(route, pd.Timedelta(time_threshold,"m"), distance_threshold))
    for poi in pois:
        res_list.append([str(poi.timestamp.hour),str(poi)])
    return res_list
