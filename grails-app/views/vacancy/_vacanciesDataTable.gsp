<el:dataTable id="vacancyTable" searchFormName="vacancySearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="vacancy"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              serviceName="vacancy">

    <el:dataTableAction accessUrl="${createLink(controller: 'vacancy', action: 'show')}"
                        functionName="renderInLineShow" actionParams="id" type="function"
                        class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show vacancy')}"/>
</el:dataTable>

<br/><br/>

</body>
</html>
