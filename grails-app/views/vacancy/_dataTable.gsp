<el:dataTable id="vacancyAdvertisementTable" searchFormName="vacancyForm"

              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="joinedVacancyAdvertisement"
              spaceBefore="true" hasRow="true" action="filter"
              serviceName="joinedVacancyAdvertisement"
              domainColumns="${domainColumns}">


    <el:dataTableAction functionName="deleteVacancy" actionParams="encodedId"
                        accessUrl="${createLink(controller:'vacancyAdvertisements',action: 'deleteVacancyFromVacancyAdvertisements')}"
                        class="red icon-trash" type="function" message="${message(code: 'default.delete.label', args: [entity], default: 'delete vacancy')}"/>
</el:dataTable>
