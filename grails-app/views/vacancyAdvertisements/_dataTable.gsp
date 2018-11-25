<el:dataTable id="vacancyAdvertisementsTable" searchFormName="vacancyAdvertisementsSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="vacancyAdvertisements"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              serviceName="vacancyAdvertisements">

    <el:dataTableAction accessUrl="${createLink(controller: 'vacancyAdvertisements', action: 'show')}"
                        functionName="renderInLineShow" actionParams="id" type="function"
                        class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show vacancyAdvertisements')}"/>
</el:dataTable>

<br/><br/>

</body>
</html>
