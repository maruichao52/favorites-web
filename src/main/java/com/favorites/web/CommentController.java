package com.favorites.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.favorites.domain.Comment;
import com.favorites.domain.CommentRepository;
import com.favorites.domain.User;
import com.favorites.domain.UserRepository;
import com.favorites.domain.result.Response;
import com.favorites.utils.DateUtils;
import com.favorites.utils.StringUtil;

@RestController
@RequestMapping("/comment")
public class CommentController extends BaseController{
	
	@Autowired
	private  CommentRepository CommentRepository;
	@Autowired
	private UserRepository userRepository;
	
	
	/**
	 * @author neo
	 * @date 2016年8月26日
	 * @param comment
	 * @return
	 */
	@RequestMapping(value="/add")
	public Response add(Comment comment) {
		if (comment.getContent().indexOf("@") > -1) {
			List<String> atUsers = StringUtil.getAtUser(comment.getContent());
			if(atUsers!=null && atUsers.size()>0){
				User user = userRepository.findByUserName(atUsers.get(0));
				if (null != user) {
					comment.setReplyUserId(user.getId());
				} else {
					logger.info("为找到匹配：" + atUsers.get(0) + "的用户.");
				}
			}
		}
		comment.setContent(comment.getContent().substring(0,comment.getContent().indexOf("@")));
		comment.setUserId(getUserId());
		comment.setCreateTime(DateUtils.getCurrentTime());
		CommentRepository.save(comment);
		return result();
	}
	
	
	/**
	 * @author neo
	 * @date 2016年8月26日
	 * @param collectId
	 * @return
	 */
	@RequestMapping(value="/list/{collectId}")
	public List<Comment> list(@PathVariable("collectId") long collectId) {
		List<Comment> comments= CommentRepository.findByCollectIdOrderByIdDesc(collectId);
		return convertComment(comments);
	}
	
	
	/**
	 * @author neo
	 * @date 2016年8月26日
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/delete/{id}")
	public Response delete(@PathVariable("id") long id) {
		CommentRepository.deleteById(id);
		return result();
	}

	
	/**
	 * 转化时间和用户名
	 * @author neo
	 * @date 2016年8月26日
	 * @param comments
	 * @return
	 */
	private List<Comment> convertComment(List<Comment> comments) {
		for (Comment comment : comments) {
			User user = userRepository.findOne(comment.getUserId());
			comment.setCommentTime(DateUtils.getTimeFormatText(comment.getCreateTime()));
			comment.setUserName(user.getUserName());
			comment.setProfilePicture(user.getProfilePicture());
		}
		return comments;
	}

}
