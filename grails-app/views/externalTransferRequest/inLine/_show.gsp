<g:render template="/externalTransferRequest/show"
          model="[externalTransferRequest:externalTransferRequest]"/>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>