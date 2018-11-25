<g:render template="/employeeStatusHistory/show"
          model="[employeeStatusHistory:employeeStatusHistory]"/>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>