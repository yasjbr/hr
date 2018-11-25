<el:dataTable id="vacationRequestTableToChooseInVacation"
              searchFormName="addVacationRequestFormInVacation"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="vacationRequest"
              action="filter"
              spaceBefore="true"
              hasRow="true" domainColumns="DOMAIN_TAB_COLUMNS"
              serviceName="vacationRequest">
</el:dataTable>

