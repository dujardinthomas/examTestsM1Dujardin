package dujardin.thomas.examTestsM1Dujardin.tools;
import java.util.List;

public interface CrudService<DAO, DTO, ID> {
    DTO create(DTO dto);
    DTO get(ID id);
    List<DTO> getAll();
    DTO update(DTO dto, ID id);
    boolean delete(ID id);

    DTO convertDaoToDTO(DAO dao);
    DAO convertDtoToDAO(DTO dto);
    
}
