<g:render template="/request/show"
          model="[request:request]"/>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>