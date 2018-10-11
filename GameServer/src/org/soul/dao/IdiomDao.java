package org.soul.dao;

import org.soul.entity.Idiom;

import java.util.List;

public interface IdiomDao {

    Idiom selectOneByValue(String value) throws Exception;

    List<Idiom> selectSomeById(int begin, int end) throws Exception;
}
