# Abstracted MCP tool integration
from langchain_core.tools import tool

@tool
def google_maps_route(origin: str, destination: str) -> dict:
    """Mock Google Maps route planning MCP tool"""
    return {"eta": "15 mins", "route": "Safe path via main road"}

@tool
def openweather_forecast(location: str) -> dict:
    """Mock OpenWeather forecast MCP tool"""
    return {"status": "Heavy rain warning", "precipitation": "120mm"}

@tool
def get_shelters(location: str) -> list:
    """Mock Filesystem MCP tool to get shelters"""
    return [{"name": "Community Center A", "capacity": 200, "status": "Open"}]
