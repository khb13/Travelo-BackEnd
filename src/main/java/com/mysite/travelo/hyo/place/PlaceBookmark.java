package com.mysite.travelo.hyo.place;

import com.mysite.travelo.yeon.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
public class PlaceBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int placeBookmarkSeq;

    @ManyToMany
    Set<User> user;

    @ManyToOne
    private Place place;
}
