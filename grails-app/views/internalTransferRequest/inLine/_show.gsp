<g:render template="/internalTransferRequest/show"
          model="[internalTransferRequest:internalTransferRequest]"/>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>