import geoDetection.stop_detection
import pandas as pd

def extract_pois(route, time_threshold, distance_threshold):
    """
    Extracts Points of Interest (POIs) from a given route based on time and distance thresholds.

    Args:
        route (geoDetection.route): The route from which to extract POIs.
        time_threshold (int): Time threshold in minutes for considering points as a POI.
        distance_threshold (float): Distance threshold in meters for considering points as a POI.

    Returns:
        list of list: List containing timestamp and string representation of each extracted POI.
    """
    res_list = []
    pois = (geoDetection.stop_detection.extract_pois(route, pd.Timedelta(time_threshold,"m"), distance_threshold))
    for poi in pois:
        res_list.append([str(poi.timestamp.timestamp()*1000),str(poi)])
    return res_list
