package pdl.backend;

import java.util.Optional;
import java.util.List;

public interface Dao<T> {

  void create(final T t ,final long userId);

  Optional<T> retrieve(final long imgId, final long userId);

  List<T> retrieveAll( final long userId);

  void update(final T t, final String[] params, final long userId);

  void delete(final T t, final long userId);

  List<Image> retrieveList(long idUser, long start, int limit);
}
