import geoDetection.point
import geoDetection.point_t
import geoDetection.route
import pandas as pd
import numpy as np

def create_Point(lat, lon, timestamp):
    """
    Creates a point using latitude, longitude, and timestamp.

    Args:
        lat (float): Latitude of the point.
        lon (float): Longitude of the point.
        timestamp (int): Timestamp of the point in milliseconds.

    Returns:
        geoDetection.point_t.PointT: Created point with specified properties.
    """
    point = geoDetection.point_t.PointT([np.deg2rad(lon), np.deg2rad(lat)], pd.to_datetime(timestamp, unit = "ms"))
    return point

def create_Route(list, timestamps):
    """
    Creates a route using a list of coordinates and corresponding timestamps.

    Args:
        coordinates (list of tuples): List of latitude-longitude tuples.
        timestamps (list of int): List of timestamps in milliseconds.

    Returns:
        geoDetection.route: Created route with specified points and timestamps.
    """
    timestamps_pandas = []
    for timestamp in timestamps:
        timestamps_pandas.append(pd.to_datetime(timestamp, unit="ms"))
    #timestamps_pandas = list(map(lambda x: pd.to_datetime(x, unit="ms"), np.array(list(timestamps))))
    return geoDetection.route.Route(route = np.array(list).tolist(), timestamps = timestamps_pandas, coordinates_unit = "radians")