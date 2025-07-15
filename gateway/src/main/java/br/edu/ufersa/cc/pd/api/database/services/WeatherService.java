package br.edu.ufersa.cc.pd.api.database.services;

import br.edu.ufersa.cc.pd.api.database.dao.BaseDao;
import br.edu.ufersa.cc.pd.api.database.dao.WeatherDao;
import br.edu.ufersa.cc.pd.api.database.entities.Weather;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WeatherService {
    BaseDao<Weather> dao;

    public WeatherService() {
        try {
            this.dao = new WeatherDao();
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing WeatherService", e);
        }
    }

    public Weather add(Weather dto) throws SQLException {
        return dao.insert(dto);
    }

    public List<Weather> list() {
        List<Weather> weathers = new ArrayList<>();
        ResultSet rs = dao.findAll();
        try {
            while (rs.next()) {
                Weather weather = new Weather();
                weather.setId(rs.getString("id"));
                weather.setWeatherData(rs.getString("weather_data"));
                weather.setRegion(rs.getString("region"));

                weathers.add(weather);
            }
            return weathers;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public boolean update(Weather weather) throws SQLException {
        return false;
    }

    public boolean delete(Weather weather) {
        return false;
    }

    public Weather search(Weather weather) {

        if (!Objects.equals(weather.getId(), "")) {
            return dao.findById(weather);
        } else
            return null;
    }

    public int get_registers_quantity() {
        return dao.getRegistersQuantity();
    }
}
