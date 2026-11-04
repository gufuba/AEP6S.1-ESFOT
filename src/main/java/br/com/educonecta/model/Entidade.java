package br.com.educonecta.model;

import java.util.Objects;

/**
 * Base de todas as entidades persistidas: concentra o identificador (ObjectId em
 * formato hexadecimal) e a igualdade baseada nele.
 */
public abstract class Entidade {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entidade outra = (Entidade) o;
        return id != null && id.equals(outra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
