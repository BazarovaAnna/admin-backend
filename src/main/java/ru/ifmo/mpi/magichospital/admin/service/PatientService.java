package ru.ifmo.mpi.magichospital.admin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.rkpunjal.sqlsafe.SqlSafeUtil;

import ru.ifmo.mpi.magichospital.admin.domain.dao.Patient;
import ru.ifmo.mpi.magichospital.admin.domain.repository.PatientRepository;
import ru.ifmo.mpi.magichospital.admin.exception.NoEntityWithSuchIdException;
import ru.ifmo.mpi.magichospital.admin.exception.PossibleSqlInjectionAttackException;

@Service
public class PatientService {
	
	@Autowired
	PatientRepository patientRepository;

	public List<Patient> getPatients() {
		List<Patient> result = new ArrayList<>();
		patientRepository.findAll().forEach(result::add);
		return result;
	}
	
	public List<Patient> getPatientsByName(String searchString) 
			throws PossibleSqlInjectionAttackException {
		String[] tokens = searchString.split(" ", 2);
		
		String token1 = tokens[0];
		String token2 = tokens.length > 1 ? tokens[1] : "";
		
		if (!SqlSafeUtil.isSqlInjectionSafe(token1) || !SqlSafeUtil.isSqlInjectionSafe(token2)) {
            throw new PossibleSqlInjectionAttackException("Possible sql injection attack!");
        }

		
		token1 = "%"+token1+"%";	
		token2 = "%"+token2+"%";
		
		return patientRepository.findByTokens(token1, token2);
	}

	public Patient getPatient(int patientId) 
			throws NoEntityWithSuchIdException {
		Optional<Patient> optionalPatient = patientRepository.findById(patientId);
		if (optionalPatient.isPresent()) {
			return optionalPatient.get();
		} else {
			throw new NoEntityWithSuchIdException("No patient with such id!");
		}

	}

}