import geoDetection.point
import geoDetection.point_t
import geoDetection.route
import pandas as pd
import numpy as np

def create_Point(lat, lon, timestamp):
    point = geoDetection.point_t.PointT([np.deg2rad(lat), np.deg2rad(lon)], pd.to_datetime(timestamp, unit = "ms"))
    return point

def create_Route(list, timestamps):
    timestamps_pandas = []
    for timestamp in timestamps:
        timestamps_pandas.append(pd.to_datetime(timestamp, unit="ms"))
    #timestamps_pandas = list(map(lambda x: pd.to_datetime(x, unit="ms"), np.array(list(timestamps))))
    return geoDetection.route.Route(route = np.array(list).tolist(), timestamps = timestamps_pandas, coordinates_unit = "radians")