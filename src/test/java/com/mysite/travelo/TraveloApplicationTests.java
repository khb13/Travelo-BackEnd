package com.mysite.travelo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.mysite.travelo.hyo.place.Place;
import com.mysite.travelo.hyo.place.PlaceRepository;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

@SpringBootTest
class TraveloApplicationTests {

    @Autowired
    private PlaceRepository placeRepository;

    @Test
    void contextLoads() {


    }


}
