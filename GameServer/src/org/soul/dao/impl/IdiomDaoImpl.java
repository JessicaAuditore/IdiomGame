package org.soul.dao.impl;

import org.soul.dao.IdiomDao;
import org.soul.database.DBAdapter;
import org.soul.database.DBOperation;
import org.soul.entity.Idiom;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class IdiomDaoImpl implements IdiomDao {

    private ResultSet rs;

    public IdiomDaoImpl() {
        rs = null;
    }

    @Override
    public Idiom selectOneByValue(String value) throws Exception {
        String sql = DBAdapter.convertSelectSql(new Idiom(value));
        rs = DBOperation.getInstance().query(sql);
        List<Object> idiomList = DBAdapter.convertResultSet(rs, Idiom.class);
        if (idiomList.size() == 0) {
            return null;
        } else {
            return (Idiom) idiomList.get(0);
        }
    }

    @Override
    public List<Idiom> selectSomeById(int begin, int end) throws Exception {
        String sql = DBAdapter.convertSelect2Sql(new Idiom(begin), new Idiom(end));
        rs = DBOperation.getInstance().query(sql);
        List<Idiom> idiomList = new ArrayList<>();
        for (Object object : DBAdapter.convertResultSet(rs, Idiom.class)) {
            idiomList.add((Idiom) object);
        }
        return idiomList;
    }
}
