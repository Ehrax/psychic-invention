package de.in.uulm.map.quartett.util;

import de.in.uulm.map.quartett.data.Attribute;
import de.in.uulm.map.quartett.data.Card;
import de.in.uulm.map.quartett.data.Deck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jona on 12/22/16.
 */

public class JsonParser {

    public static Deck parseDeck(JSONObject deck) throws JSONException {

        // maybe add the names of the elements to strings.xml

        JSONArray jsonCards = deck.getJSONArray("cards");
        JSONArray jsonAttributes = deck.getJSONArray("properties");

        ArrayList<Attribute> attrs = new ArrayList<>();

        for(int i = 0; i < jsonAttributes.length(); i++) {
            attrs.add(parseAttribute(
                    (JSONObject) jsonAttributes.get(i)));
        }

        ArrayList<Card> cards = new ArrayList<>();

        for(int i = 0; i < jsonCards.length(); i++) {
            cards.add(parseCard(
                    (JSONObject) jsonCards.get(i)));
        }

        return null;
    }

    public static Card parseCard(JSONObject card) {

        return null;
    }

    public static Attribute parseAttribute(JSONObject attr) {

        return null;
    }

}
