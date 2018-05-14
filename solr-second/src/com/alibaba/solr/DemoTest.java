package com.alibaba.solr;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

/**
 * solr的增删改查
 * @author wen
 *
 */
public class DemoTest {
	/**
	 * 增加
	 * @throws Exception 
	 * @throws SolrServerException 
	 */
	@Test
	public void testAdd() throws Exception {
		SolrServer solrServer = new HttpSolrServer("http://localhost:8585/solr/collection1");
		for(int i=11;i<=20;i++) {
			SolrInputDocument doc= new SolrInputDocument();
			doc.addField("id", i+"");
			doc.addField("name", "solr添加标题"+i);
			doc.addField("content", "solr添加内容"+i);
			solrServer.add(doc);
		}
		solrServer.commit();
	}
	
	/**
	 * 修改
	 * @throws Exception 
	 * @throws SolrServerException 
	 */
	@Test
	public void testUpdate() throws Exception {
		SolrServer solrServer = new HttpSolrServer("http://localhost:8585/solr/collection1");
		SolrInputDocument doc= new SolrInputDocument();
		doc.addField("id", "1");
		doc.addField("name", "solr添加标题-修改后");
		doc.addField("content", "solr添加内容-修改后");
		solrServer.add(doc);
		solrServer.commit();
	}
	
	/**
	 * 删除
	 * @throws Exception 
	 * @throws SolrServerException 
	 */
	@Test
	public void testDelete() throws Exception {
		SolrServer solrServer = new HttpSolrServer("http://localhost:8585/solr/collection1");
//		solrServer.deleteById("1");
		//solr添加标题10 这个地方给这个词自动分词了 solr 添 加 标 题 10  name:solr name:添	name:加	name:标	name:题
		solrServer.deleteByQuery("name:solr添加标题10");
		solrServer.commit();
	}
	
	/**
	 * 查询
	 * @throws Exception 
	 * @throws SolrServerException 
	 */
	@Test
	public void testQuery() throws Exception {
		SolrServer solrServer = new HttpSolrServer("http://localhost:8585/solr/collection1");
		SolrQuery params = new SolrQuery();
//		params.setQuery("name:solr");//写得是语法
		//这个方法有一个默认的查询域
		//精确查找的词
		params.set("q", "小黄人");
		//默认查询的域
		params.set("df", "product_keywords");
		//添加过滤信息
//		params.addFilterQuery("product_catalog_name:美味厨房");
//		params.addFilterQuery("product_price:[* TO 10]");
		//设置分页的信息
		params.setStart(0);
		params.setRows(10);
		//排序
		params.addSort("product_price", ORDER.asc);
		//开启高亮
		params.setHighlight(true);
		//设置高亮的样式
		params.setHighlightSimplePre("<span style='color:red'>");
		params.setHighlightSimplePost("</span>");
		//设置高亮显示的域
		params.addHighlightField("product_name");
		
		
		QueryResponse queryResponse = solrServer.query(params);
//		"highlighting": {
//		    "5498": {
//		      "product_name": [
//		        "<span style='color:red'>花儿</span>朵朵苹果切瓣器开瓣器开果器109"
//		      ]
//		    }
//		  }
		Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
		
		SolrDocumentList list = queryResponse.getResults();
		System.out.println("总条数"+list.getNumFound());
		for (SolrDocument solrDocument : list) {
//			<field name="product_name" type="text_ik" indexed="true" stored="true"/>
//			<field name="product_price"  type="float" indexed="true" stored="true"/>
//			<field name="product_description" type="text_ik" indexed="true" stored="false" />
//			<field name="product_picture" type="string" indexed="false" stored="true" />
//			<field name="product_catalog_name" type="string" indexed="true" stored="true" />
			
			Map<String, List<String>> map = highlighting.get(solrDocument.get("id"));
			List<String> mapList = map.get("product_name");
			String product_name = "";
			if(mapList != null && mapList.size() > 0) {
				product_name = mapList.get(0);
			}else {
				product_name = (String) solrDocument.get("product_name");
			}
			System.out.println(solrDocument.get("id"));
			System.out.println(product_name);
			System.out.println(solrDocument.get("product_price"));
			System.out.println(solrDocument.get("product_picture"));
			System.out.println(solrDocument.get("product_catalog_name"));
		}
	}
	
	
	
	
//	"params": {
//	      "q": "花儿",
//	      "df": "product_keywords",
//	      "indent": "true",
//	      "start": "0",
//	      "rows": "5"
//	      "_": "1526115856596",
//	      "hl.simple.pre": "<span style='color:red'>",
//	      "hl.simple.post": "</span>",
//	      "hl.fl": "product_name",
//	      "wt": "json",
//	      "hl": "true",
//	      "fq": [
//	        "product_catalog_name:美味厨房",
//	        "product_price:[* TO 10]"
//	      ],
//	    }
//	  }

}
