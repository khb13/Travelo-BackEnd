package com.mysite.travleo;

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

        String areaStr[] = {"1", "6", "4", "2", "5", "3", "7", "8", "31", "32", "33", "34", "35", "36", "37", "38", "39"};
        String typeStr[] = {"12", "14", "28", "32", "38", "39"};

        APICaller apiCaller = new APICaller();

        for (int i = 0; i < 17; i++) {
            for (int j = 0; j < 6; j++) {
                String urlString = "http://apis.data.go.kr/B551011/KorService1/areaBasedList1?serviceKey=x%2B0zQQWisDDkH80KSlVVK8w3Fefbbu9JZ644XFw74RptRvBCFLGh6iDWXDfCAH9Kgz0kUePR45JtZf1R90gKmw%3D%3D"
                        + "&pageNo=1&numOfRows=100&MobileApp=AppTest&MobileOS=ETC&arrange=A"
                        + "&contentTypeId=" + typeStr[j]
                        + "&areaCode=" + areaStr[i];

                try {
                    String apiResponse = apiCaller.callAPI(urlString);

                    // XML 파싱 시작
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(new InputSource(new StringReader(apiResponse)));

                    // XML에서 필요한 요소 추출
                    NodeList items = doc.getElementsByTagName("item");

                    for (int k = 0; k < items.getLength(); k++) {
                        Node itemNode = items.item(k);
                        if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element itemElement = (Element) itemNode;

                            // 필요한 데이터 추출
                            String pAreaCode = getTextContent(itemElement, "areacode");
                            String type = getTextContent(itemElement, "contenttypeid");
                            String title = getTextContent(itemElement, "title");
                            String tel = getTextContent(itemElement, "tel");
                            String zipCode = getTextContent(itemElement, "zipcode");
                            String address = getTextContent(itemElement, "addr1");
                            String address_detail = getTextContent(itemElement, "addr2");
                            double longitude = getDoubleContent(itemElement, "mapx");
                            double latitude = getDoubleContent(itemElement, "mapy");
                            String imageFile1 = getTextContent(itemElement, "firstimage");
                            String imageFile2 = getTextContent(itemElement, "firstimage2");

                            // Place 객체 생성 및 설정
                            Place place = new Place();
                            place.setAreaCode(pAreaCode);
                            place.setType(type);
                            place.setTitle(title);
                            place.setTel(tel);
                            place.setZipCode(zipCode);
                            place.setAddress(address);
                            place.setAddressDetail(address_detail);
                            place.setLongitude(longitude);
                            place.setLatitude(latitude);
                            place.setImageFile1(imageFile1);
                            place.setImageFile2(imageFile2);
                            place.setViewCount(0);
                            place.setLikeCount(0);

                            // 데이터베이스에 저장
                            placeRepository.save(place);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // 예외 처리 코드를 추가합니다.
                }
            }
        }
    }

    // XML Element에서 특정 태그의 텍스트 콘텐츠를 가져오는 메서드
    private String getTextContent(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }

    // XML Element에서 특정 태그의 double 형식 콘텐츠를 가져오는 메서드
    private double getDoubleContent(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return Double.parseDouble(nodeList.item(0).getTextContent());
        }
        return 0.0;
    }

    // API 호출 클래스
    private class APICaller {

        public String callAPI(String urlString) throws Exception {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return response.toString();
        }
    }
}
