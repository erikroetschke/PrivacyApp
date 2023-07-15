import geoDetection.stop_detection
import pandas as pd

def extract_pois(route):
    return geoDetection.stop_detection.extract_pois(route, pd.Timedelta(7,"m"), 50)