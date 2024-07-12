package com.mysite.travelo.hyo.place;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

import com.mysite.travelo.yeon.user.SiteUser;

@Getter
@Setter
@Entity
public class PlaceBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int placeBookmarkSeq;

    @ManyToOne
    private SiteUser user;

    @ManyToOne
    private Place place;
}
