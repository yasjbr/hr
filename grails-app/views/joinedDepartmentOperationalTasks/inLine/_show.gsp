
<g:render template="/joinedDepartmentOperationalTasks/show" model="[joinedDepartmentOperationalTasks:joinedDepartmentOperationalTasks]"/>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:editButton accessUrl="${createLink(controller: tabEntityName,action: 'edit')}"  onClick="renderInLineEdit('${joinedDepartmentOperationalTasks?.id}')"/>
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>