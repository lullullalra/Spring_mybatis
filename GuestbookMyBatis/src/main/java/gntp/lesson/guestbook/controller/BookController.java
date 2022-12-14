package gntp.lesson.guestbook.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import gntp.lesson.guestbook.dao.GuestbookDAO;
import gntp.lesson.guestbook.vo.GuestbookVO;
import gntp.lesson.guestbook.vo.ReplyVO;

@Controller("bookController")
@RequestMapping("/guestbook")
public class BookController {
	
	@Autowired
	private GuestbookDAO guestbookDAO;
	
	@RequestMapping(value="/listBook.do", method=RequestMethod.GET)
	public ModelAndView listBook(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		List<GuestbookVO> list = guestbookDAO.selectAll();
		mav.addObject("list", list);
		String viewName = this.getViewName(request);
		mav.setViewName("listBook");
		return mav;
	}
	
	@RequestMapping(value="/deleteBook.do", method=RequestMethod.GET)
	public ModelAndView deleteBook(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		String seq = request.getParameter("seq");
		guestbookDAO.deleteOne(seq);
		
		List<GuestbookVO> list = guestbookDAO.selectAll();
		mav.addObject("list", list);
		
		mav.setViewName("listBook");
		return mav;
	}
	
	@RequestMapping(value="/writeBook.do", method=RequestMethod.GET)
	public ModelAndView writeBook(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		
		mav.setViewName("writeBook");
		return mav;
	}
	
	@RequestMapping(value="/readBook.do", method=RequestMethod.GET)
	public ModelAndView readBook(HttpServletRequest request, HttpServletResponse response) throws SQLException {
		ModelAndView mav = new ModelAndView();
		GuestbookVO book = null;
		String seq = request.getParameter("seq");
		String token = request.getParameter("token");
		System.out.println(seq);
		System.out.println(token);
		book = guestbookDAO.selectOne(seq, token);
		mav.addObject("book", book);
		
		mav.setViewName("readBook");
		return mav;
	}
	
	@RequestMapping(value="/insertBook.do", method=RequestMethod.POST)
	public ModelAndView insertBook(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String userId = request.getParameter("userId");
		
		GuestbookVO book = new GuestbookVO();
		book.setTitle(title);
		book.setContent(content);
		book.setUserId(userId);
		
		guestbookDAO.insertOne(book);

		List<GuestbookVO> list = guestbookDAO.selectAll();
		mav.addObject("list", list);
		
		mav.setViewName("listBook");
		
		return mav;
	}
	
	@RequestMapping(value="/viewUpdateBook.do", method=RequestMethod.POST)
	public ModelAndView viewUpdateBook(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		int seq = Integer.parseInt(request.getParameter("seq"));
		GuestbookVO book = guestbookDAO.selectOneForUpdate(seq);
		mav.addObject("book", book);

		mav.setViewName("viewUpdateBook");
		return mav;
	}
	
	@RequestMapping(value="/updateBook.do", method=RequestMethod.POST)
	public ModelAndView updateBook(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView();
		int seq = Integer.parseInt(request.getParameter("seq"));
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		int readCount = Integer.parseInt(request.getParameter("readCount"));
		GuestbookVO book = new GuestbookVO();
		book.setTitle(title);
		book.setContent(content);
		book.setReadCount(readCount);
		book.setSeq(seq);
		guestbookDAO.updateOne(book);
		
		List<GuestbookVO> list = guestbookDAO.selectAll();
		mav.addObject("list", list);
		
		mav.setViewName("listBook");
		return mav;
	}
	
	@RequestMapping(value="/writeReply.do", method=RequestMethod.POST)
	public ModelAndView writeReply(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		String reply = request.getParameter("reply");
		String seq = request.getParameter("seq");
		ReplyVO vo = new ReplyVO();
		vo.setSeq(Integer.parseInt(seq));
		vo.setContent(reply);
		System.out.println(seq);
		System.out.println(reply);
		guestbookDAO.insertReply(vo);
		
		mav.setViewName("readBook");
		return mav;
	}

	
	private  String getViewName(HttpServletRequest request) throws Exception {
	      String contextPath = request.getContextPath();
	      String uri = (String)request.getAttribute("javax.servlet.include.request_uri");
	      if(uri == null || uri.trim().equals("")) {
	         uri = request.getRequestURI();
	      }
	      
	      //http://localhost:8090/member/listMember.do??? ?????????
	      int begin = 0;  //
	      if(!((contextPath==null)||("".equals(contextPath)))){
	         begin = contextPath.length();  // ?????? ????????? ??? ????????? ??????
	      }

	      int end;
	      if(uri.indexOf(";")!=-1){
	         end=uri.indexOf(";");  //?????? uri??? ';'??? ?????? ?????? ';'?????? ????????? ??????
	      }else if(uri.indexOf("?")!=-1){
	         end=uri.indexOf("?");   //?????? uri??? '?'??? ?????? ?????? '?' ?????? ????????? ??????
	      }else{
	         end=uri.length();
	      }

	      //http://localhost:8090/member/listMember.do??? ????????? ?????? '.do'??? ????????? http://localhost:8090/member/listMember??? ?????? ???,
	      //?????? http://localhost:8090/member/listMember?????? ???????????? ????????? '/' ????????? ?????? ???, ??? ?????? listMember??? ?????????.
	      String fileName=uri.substring(begin,end);
	      if(fileName.indexOf(".")!=-1){
	         fileName=fileName.substring(0,fileName.lastIndexOf("."));  //??????????????? ???????????? ?????? '.'??? ????????? ?????????, '.do' ??????????????? ???????????? ??????
	      }
	      if(fileName.lastIndexOf("/")!=-1){
	         fileName=fileName.substring(fileName.lastIndexOf("/"),fileName.length()); //??????????????? ???????????? ?????? '/'??? ????????? ?????????, '/' ??????????????? ???????????? ??????  
	      }
	      return fileName;
	   }
}
