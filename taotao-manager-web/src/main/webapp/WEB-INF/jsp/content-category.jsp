<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div>
	<!-- 被 # 选择的标签 -->
	 <ul id="contentCategory" class="easyui-tree">  </ul>
</div>
<div id="contentCategoryMenu" class="easyui-menu" style="width:120px;" data-options="onClick:menuHandler">
    <div data-options="iconCls:'icon-add',name:'add'">添加</div>
    <div data-options="iconCls:'icon-remove',name:'rename'">重命名</div>
    <div class="menu-sep"></div>
    <div data-options="iconCls:'icon-remove',name:'delete'">删除</div>
</div>
<script type="text/javascript">
//在文档加载后处理以下逻辑
$(function(){
	
	// “ # ” 为id选择器  .tree将分层数据以树形结构显示
	$("#contentCategory").tree({
		url : '/content/category/list',
		animate: true,     //树动画
		method : "GET",
		
		//右击鼠标触发
		onContextMenu: function(e,node){
		
            e.preventDefault();  //关闭原来默认右击选项
        
            $(this).tree('select',node.target);  //选中右击鼠标的节点
            
            $('#contentCategoryMenu').menu('show',{  //展示菜单栏
                left: e.pageX,  //在鼠标位置显示
                top: e.pageY   //在鼠标位置显示
            });
        },
        
        //在选中事件需要编辑时触发逻辑。 
        //使新增节点与父节点产生关系，设置新增节点的 父id 自身id
        onAfterEdit : function(node){
        	var _tree = $(this);  //获取树本身
        	if(node.id == 0){
        		// 新增节点
        		$.post("/content/category/create",{parentId:node.parentId,name:node.text},function(data){
        			if(data.status == 200){
        				_tree.tree("update",{
        					//更新节点
            				target : node.target,  //被更新的节点
            				id : data.data.id   //新增节点的id
            			});
        			}else{
        				$.messager.alert('提示','创建'+node.text+' 分类失败!');
        			}
        		});
        	}else{
        		$.post("/content/category/update",{id:node.id,name:node.text});
        	}
        }
	});
});

//处理点击菜单的事件
//item为事件名称
function menuHandler(item){
	//获取树
	var tree = $("#contentCategory");
	//获取被选中的节点（右击鼠标时选中的事件）
	var node = tree.tree("getSelected");
	if(item.name === "add"){  //当事件为添加节点时
		tree.tree('append', {    //在被点击的节点下追加一个子节点
            parent: (node?node.target:null),   // .target表示为当前DOM对象 
            data: [{
                text: '新建分类',  //当前节点内容
                id : 0,   //当前节点id
                parentId : node.id  //父节点id
            }]
        }); 
		//找到id为0的节点（新创建的节点）
		var _node = tree.tree('find',0);
		//选中此节点，开始编辑节点.(编辑节点文本)
		tree.tree("select",_node.target).tree('beginEdit',_node.target);
	}else if(item.name === "rename"){
		tree.tree('beginEdit',node.target);
	}else if(item.name === "delete"){
		$.messager.confirm('确认','确定删除名为 '+node.text+' 的分类吗？',function(r){
			if(r){
				$.post("/content/category/delete/",{id:node.id},function(TaotaoResult){  //post请求后台删除节点数据
					if(TaotaoResult.status == 200){
						tree.tree("remove",node.target);//后台删除数据后，前面页面也删除该节点
					}else{
						$.messager.alert('提示',TaotaoResult.msg);
					}
				});	
			}
		});
	}
}
</script>