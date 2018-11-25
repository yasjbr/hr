<g:render template="/employeeInternalAssignation/show"
          model="[employeeInternalAssignation:employeeInternalAssignation]"/>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>