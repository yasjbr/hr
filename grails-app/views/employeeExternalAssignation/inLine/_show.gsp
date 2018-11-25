<g:render template="/employeeExternalAssignation/show"
          model="[employeeExternalAssignation:employeeExternalAssignation]"/>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>