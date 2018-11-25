<g:render template="/employeeSalaryInfo/show"
          model="[employeeSalaryInfo:employeeSalaryInfo]"/>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>