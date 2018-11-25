<g:render template="/joinedEmployeeOperationalTasks/show"
          model="[joinedEmployeeOperationalTasks:joinedEmployeeOperationalTasks]"/>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:editButton accessUrl="${createLink(controller: tabEntityName,action: 'edit')}"  onClick="renderInLineEdit('${joinedEmployeeOperationalTasks?.id}')"/>
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>