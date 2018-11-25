<g:render template="/employmentServiceRequest/show"
          model="[employmentServiceRequest:employmentServiceRequest]"/>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>