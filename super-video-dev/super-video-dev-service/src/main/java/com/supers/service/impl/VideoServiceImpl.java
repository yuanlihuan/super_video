package com.supers.service.impl;

import java.util.List;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.supers.mapper.SearchRecordsMapper;
import com.supers.mapper.UsersLikeVideosMapper;
import com.supers.mapper.UsersMapper;
import com.supers.mapper.VideosMapper;
import com.supers.mapper.custom.VideosMapperCustom;
import com.supers.pojo.SearchRecords;
import com.supers.pojo.UsersLikeVideos;
import com.supers.pojo.Videos;
import com.supers.pojo.vo.VideoVo;
import com.supers.service.VideoService;
import com.supers.utils.PagedResult;

@Service
public class VideoServiceImpl implements VideoService {

	@Autowired
	private Sid sid;
	
	@Autowired
	private VideosMapper videosMapper;
	
	@Autowired
	private UsersMapper usersMapper;
	
	@Autowired
	private VideosMapperCustom videosMapperCustom;
	
	@Autowired
	private SearchRecordsMapper searchRecordsMapper;
	
	@Autowired
	private UsersLikeVideosMapper usersLikeVideosMapper;
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public String insert(Videos videos) {
		
		videos.setId(sid.nextShort());
		videosMapper.insert(videos);
		return videos.getId();
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void updateById(Videos video) {
		videosMapper.updateByPrimaryKeySelective(video);
	}

	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public PagedResult queryAllVideo(Videos video,String userId, Integer isSaveSearch, Integer page, Integer pageSize) {
		
		//isSaveSearch=1保存，=0或者为空不保存
		String desc = "";
		if(isSaveSearch == 1) {
			SearchRecords searchRecords = new SearchRecords();
			desc = video.getVideoDesc();
			searchRecords.setId(sid.nextShort());
			searchRecords.setContent(desc);
			searchRecordsMapper.insert(searchRecords);
		}
		
		PageHelper.startPage(page, pageSize);
		List<VideoVo> list = videosMapperCustom.queryAllVideos(desc, userId);
		PageInfo<VideoVo> pageInfo = new PageInfo<VideoVo>(list);
		
		PagedResult pagedResult = new PagedResult();
		pagedResult.setPage(page);
		pagedResult.setRecords(pageInfo.getTotal());
		pagedResult.setTotal(pageInfo.getPages());
		pagedResult.setRows(list);
		return pagedResult;
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public List<String> getHotContent() {
		
		return searchRecordsMapper.getHotContentList();
	}

	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public void addVideoLikeCounts(String userId, String videoId,
			String videoCreateId) {
		//将数据插入user_like_video表中
		String id = sid.nextShort();
	    UsersLikeVideos usersLikeVideos = new UsersLikeVideos();
	    usersLikeVideos.setId(id);
	    usersLikeVideos.setUserId(userId);
	    usersLikeVideos.setVideoId(videoId);
		int insert = usersLikeVideosMapper.insert(usersLikeVideos);
		
		if (insert == 1) {
			//插入成功修改video喜欢加1
			videosMapperCustom.addVideoLikeCounts(videoId);
			//插入user喜欢点赞加1
			usersMapper.addUserLikeCounts(videoCreateId);
		}
		
	}

	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public void reduceVideoLikeCounts(String userId, String videoId,
			String videoCreateId) {
		//将数据删除user_like_video表中
 	    Example example = new Example(UsersLikeVideos.class);
 	    Criteria criteria = example.createCriteria();
 	    criteria.andEqualTo("userId", userId);
 	    criteria.andEqualTo("videoId", videoId);
		int insert = usersLikeVideosMapper.deleteByExample(example);
		
		if (insert == 1) {
			//插入成功修改video喜欢加1
			videosMapperCustom.reduceVideoLikeCounts(videoId);
			//插入user喜欢点赞加1
			usersMapper.reduceUserLikeCounts(videoCreateId);
		}
		
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public PagedResult collectionAllVideo(String userId, Integer page,
			Integer pageSize) {
		PageHelper.startPage(page, pageSize);
		
		List<VideoVo> list = videosMapperCustom.collectionAllVideos(userId);
		PageInfo<VideoVo> pageInfo = new PageInfo<VideoVo>(list);
		
		PagedResult pagedResult = new PagedResult();
		pagedResult.setPage(page);
		pagedResult.setRecords(pageInfo.getTotal());
		pagedResult.setTotal(pageInfo.getPages());
		pagedResult.setRows(list);
		return pagedResult;
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public PagedResult followAllVideo(String userId, Integer page,
			Integer pageSize) {
		PageHelper.startPage(page, pageSize);
		
		List<VideoVo> list = videosMapperCustom.followAllVideos(userId);
		PageInfo<VideoVo> pageInfo = new PageInfo<VideoVo>(list);
		
		PagedResult pagedResult = new PagedResult();
		pagedResult.setPage(page);
		pagedResult.setRecords(pageInfo.getTotal());
		pagedResult.setTotal(pageInfo.getPages());
		pagedResult.setRows(list);
		return pagedResult;
	}



}
