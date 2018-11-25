<g:render template="/suspensionRequest/show"
          model="[suspensionRequest:suspensionRequest]"/>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>