<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="easyui-panel" title="Nested Panel" data-options="width:'100%',minHeight:500,noheader:true,border:false" style="padding:10px;">
    <div class="easyui-layout" data-options="fit:true">
        <div data-options="region:'west',split:false" style="width:250px;padding:5px">
            <ul id="contentCategoryTree" class="easyui-tree" data-options="url:'/content/category/list',animate: true,method : 'GET'">
            </ul>
        </div>
        <div data-options="region:'center'" style="padding:5px">
            <table class="easyui-datagrid" id="contentList" 
            data-options="toolbar:contentListToolbar,singleSelect:false,collapsible:true,pagination:true,method:'get',pageSize:20,url:'/content/query/list',queryParams:{categoryId:0}">
		    <thead>
		        <tr>
		            <th data-options="field:'id',width:30">ID</th>
		            <th data-options="field:'title',width:120">内容标题</th>
		            <th data-options="field:'subTitle',width:100">内容子标题</th>
		            <th data-options="field:'titleDesc',width:120">内容描述</th>
		            <th data-options="field:'url',width:60,align:'center',formatter:TAOTAO.formatUrl">内容连接</th>
		            <th data-options="field:'pic',width:50,align:'center',formatter:TAOTAO.formatUrl">图片</th>
		            <th data-options="field:'pic2',width:50,align:'center',formatter:TAOTAO.formatUrl">图片2</th>
		            <th data-options="field:'created',width:130,align:'center',formatter:TAOTAO.formatDateTime">创建日期</th>
		            <th data-options="field:'updated',width:130,align:'center',formatter:TAOTAO.formatDateTime">更新日期</th>
		        </tr>
		    </thead>
		</table>
        </div>
    </div>
</div>
<script type="text/javascript">
$(function(){
	var tree = $("#contentCategoryTree");
	var datagrid = $("#contentList");
	tree.tree({
		//在创建树时请求url 	'/content/category/list'
		//在点击节点时触发
		onClick : function(node){
			if(tree.tree("isLeaf",node.target)){   //如果是叶子节点
				datagrid.datagrid('reload', {   //重新发送url，加载表格中数据，
					categoryId :node.id   //参数为categoryId	
		        });
			}
		}
	});
});
var contentListToolbar = [{
    text:'新增',
    iconCls:'icon-add',
    
    //逻辑： 未选择节点，或选择父节点时，提示需要选择一个内容分类
    //Handler 点击事件时触发
    handler:function(){  
    	//表示获取树中被选择的节点
    	var node = $("#contentCategoryTree").tree("getSelected");
    	//  !node：节点为空
    	//  !$("#contentCategoryTree").tree("isLeaf",node.target)：被选择的节点不是叶子节点
    	if(!node || !$("#contentCategoryTree").tree("isLeaf",node.target)){
    		$.messager.alert('提示','新增内容必须选择一个内容分类!');
    		return ;
    	}
    	
    	//选中叶子节点时处理以下逻辑
    	//创建窗口
    	TT.createWindow({
			url : "/content-add"
		}); 
    }
},{
    text:'编辑',
    iconCls:'icon-edit',
    handler:function(){
    	//获取被选中的内容的id集合，若为空，表示未选中内容 
    	var ids = TT.getSelectionsIds("#contentList");
    	if(ids.length == 0){
    		$.messager.alert('提示','必须选择一个内容才能编辑!');
    		return ;
    	}
    	if(ids.indexOf(',') > 0){
    		$.messager.alert('提示','只能选择一个内容!');
    		return ;
    	}
		TT.createWindow({
			url : "/content-edit",
			onLoad : function(){
				var data = $("#contentList").datagrid("getSelections")[0];
				$("#contentEditForm").form("load",data);
				
				// 实现图片
				if(data.pic){
					$("#contentEditForm [name=pic]").after("<a href='"+data.pic+"' target='_blank'><img src='"+data.pic+"' width='80' height='50'/></a>");	
				}
				if(data.pic2){
					$("#contentEditForm [name=pic2]").after("<a href='"+data.pic2+"' target='_blank'><img src='"+data.pic2+"' width='80' height='50'/></a>");					
				}
				
				contentEditEditor.html(data.content);
			}
		});    	
    }
},{
    text:'删除',
    iconCls:'icon-cancel',
    handler:function(){
    	var ids = TT.getSelectionsIds("#contentList");
    	if(ids.length == 0){
    		$.messager.alert('提示','未选中商品!');
    		return ;
    	}
    	$.messager.confirm('确认','确定删除ID为 '+ids+' 的内容吗？',function(r){
    	    if (r){
    	    	var params = {"ids":ids};
            	$.post("/content/delete",params, function(data){
        			if(data.status == 200){
        				$.messager.alert('提示','删除内容成功!',undefined,function(){
        					$("#contentList").datagrid("reload");
        				});
        			}
        		});
    	    }
    	});
    }
}];
</script>