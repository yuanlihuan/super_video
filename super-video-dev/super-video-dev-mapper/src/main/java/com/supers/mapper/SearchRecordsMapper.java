package com.supers.mapper;

import java.util.List;

import com.supers.pojo.SearchRecords;
import com.supers.utils.MyMapper;

public interface SearchRecordsMapper extends MyMapper<SearchRecords> {

	List<String> getHotContentList();
}