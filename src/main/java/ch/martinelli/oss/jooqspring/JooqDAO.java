package ch.martinelli.oss.jooqspring;

import org.jooq.*;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.jooq.impl.DSL.row;

/**
 * Abstract DAO class for CRUD operations using jOOQ.
 * <a href="https://en.wikipedia.org/wiki/Data_access_object">DAO on Wikipedia</a>
 *
 * @param <T>  the type of the jOOQ Table
 * @param <R>  the type of the jOOQ UpdatableRecord
 * @param <ID> the type of the primary key
 */
@Transactional(readOnly = true)
public abstract class JooqDAO<T extends Table<R>, R extends UpdatableRecord<R>, ID> {

    /**
     * The DSLContext instance used for executing SQL queries and interacting with the database.
     * It serves as the primary interface for jOOQ operations.
     */
    protected final DSLContext dslContext;
    /**
     * The database table associated with this repository.
     * Represents the specific table in the database that this repository interacts with.
     * Used for performing CRUD operations.
     */
    protected final T table;

    /**
     * The version field used for optimistic locking.
     * Represents the column in the database table that stores the version number for each record.
     * Used to implement optimistic locking to prevent concurrent updates from overwriting each other.
     */
    protected final TableField<R, Long> versionField;

    /**
     * Constructs a new JooqRepository with the specified DSLContext and table.
     *
     * @param dslContext the DSLContext to be used for database operations
     * @param table      the table associated with this repository
     */
    public JooqDAO(DSLContext dslContext, T table) {
        this(dslContext, table, null);
    }

    /**
     * Constructs a new JooqRepository with the specified DSLContext, table and version field.
     *
     * @param dslContext the DSLContext to be used for database operations
     * @param table      the table associated with this repository
     * @param versionField the version field used for optimistic locking
     */
    public JooqDAO(DSLContext dslContext, T table, TableField<R, Long> versionField) {
        this.dslContext = dslContext;
        this.table = table;
        this.versionField = versionField;
    }

    /**
     * Checks whether a record exists with the specified primary key.
     *
     * @param id the primary key value of the record to check
     * @return {@code true} if a record with the specified primary key exists,
     * {@code false} otherwise
     * @throws IllegalArgumentException if the table does not have a primary key
     */
    public boolean existsById(ID id) {
        if (table.getPrimaryKey() == null) {
            throw new IllegalArgumentException("This method can only be called on tables with a primary key");
        }
        return dslContext.fetchExists(table, eq(table.getPrimaryKey(), id));
    }

    /**
     * Finds a record by its primary key.
     *
     * @param id the primary key value of the record to find
     * @return an Optional containing the found record, or empty if no record was found
     * @throws IllegalArgumentException if the table does not have a primary key
     */
    public Optional<R> findById(ID id) {
        if (table.getPrimaryKey() == null) {
            throw new IllegalArgumentException("This method can only be called on tables with a primary key");
        }
        return dslContext
                .selectFrom(table)
                .where(eq(table.getPrimaryKey(), id))
                .fetchOptional();
    }

    /**
     * Retrieves a list of records from the database with pagination and sorting.
     *
     * @param offset  the starting position of the first record
     * @param limit   the maximum number of records to retrieve
     * @param orderBy the list of fields to order the result set by
     * @return a List containing the fetched records
     */
    public List<R> findAll(int offset, int limit, List<OrderField<?>> orderBy) {
        return dslContext
                .selectFrom(table)
                .orderBy(orderBy)
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * Retrieves a list of records from the database with filtering, pagination, and sorting.
     *
     * @param condition the condition to filter the records by
     * @param offset    the starting position of the first record
     * @param limit     the maximum number of records to retrieve
     * @param orderBy   the list of fields to order the result set by
     * @return a List containing the fetched records
     */
    public List<R> findAll(Condition condition, int offset, int limit, List<OrderField<?>> orderBy) {
        return dslContext
                .selectFrom(table)
                .where(condition)
                .orderBy(orderBy)
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * Retrieves a list of records from the database with filtering.
     *
     * @param condition the condition to filter the records by
     * @return a List containing the fetched records
     */
    public List<R> findAll(Condition condition) {
        return dslContext
                .selectFrom(table)
                .where(condition)
                .fetch();
    }

    /**
     * Retrieves a list of records from the database with filtering, and sorting.
     *
     * @param condition the condition to filter the records by
     * @param orderBy   the list of fields to order the result set by
     * @return a List containing the fetched records
     */
    public List<R> findAll(Condition condition, List<OrderField<?>> orderBy) {
        return dslContext
                .selectFrom(table)
                .where(condition)
                .orderBy(orderBy)
                .fetch();
    }

    /**
     * Counts the total number of records in the associated table.
     *
     * @return the total number of records in the table
     */
    public int count() {
        return dslContext.fetchCount(table);
    }

    /**
     * Counts the number of records in the associated table that match the given condition.
     *
     * @param condition the condition to filter the records by
     * @return the number of matching records
     */
    public int count(Condition condition) {
        return dslContext.fetchCount(table, condition);
    }

    /**
     * Saves (INSERT or UPDATE) the given record to the database. Attaches the record to the
     * DSLContext and stores it.
     *
     * @param record the record to save
     * @return the number of affected rows
     */
    @Transactional
    public int save(R record) {
        dslContext.attach(record);
        return record.store();
    }

    /**
     * Saves a list of records to the database using batch store operations.
     *
     * @param records the list of records to be saved
     * @return an array containing the number of affected rows for each batch operation
     */
    @Transactional
    public int[] saveAll(List<R> records) {
        return dslContext.batchStore(records).execute();
    }

    /**
     * Merges (INSERT … ON DUPLICATE KEY UPDATE) the given record into the database. Attaches the record to the DSLContext
     * and attempts to merge it.
     *
     * @param record the record to merge
     * @return the number of affected rows
     */
    @Transactional
    public int merge(R record) {
        dslContext.attach(record);
        return record.merge();
    }

    /**
     * Updates a record using optimistic locking.
     *
     * @param id              the primary key
     * @param expectedVersion the version originally read
     * @param record          the record containing the fields and values to update
     * @return the number of affected rows (1 on success)
     * @throws IllegalStateException if optimistic locking is not configured
     * @throws ConcurrencyFailureException if the record is missing or its version has changed
     */
    @Transactional
    public int updateOptimistically(ID id, Long expectedVersion, R record) {
        if (versionField == null) {
            throw new IllegalStateException("Optimistic locking is not configured for this table");
        }

        if (table.getPrimaryKey() == null) {
            throw new IllegalArgumentException("This method can only be called on tables with a primary key");
        }

        Objects.requireNonNull(expectedVersion, "expectedVersion");
        Objects.requireNonNull(record, "record");

        if (expectedVersion < 0) {
            throw new IllegalArgumentException("expectedVersion must not be negative");
        }

        if (!record.touched()) {
            throw new IllegalArgumentException("At least one field must be updated");
        }

        if (record.touched(versionField)) {
            throw new IllegalArgumentException("The version field is managed automatically");
        }

        int updated = dslContext
                .update(table)
                .set(record)
                .set(versionField, versionField.add(1L))
                .where(
                        eq(table.getPrimaryKey(), id)
                                .and(versionField.eq(expectedVersion))
                )
                .execute();

        if (updated == 0) {
            throw new ConcurrencyFailureException("Optimistic locking failed for id " + id + " at version " + expectedVersion);
        }

        return updated;
    }

    /**
     * Deletes the specified record from the database.
     *
     * @param record the record to delete
     * @return the number of affected rows
     */
    @Transactional
    public int delete(R record) {
        dslContext.attach(record);
        return record.delete();
    }

    /**
     * Deletes a record from the database identified by its primary key.
     *
     * @param id the primary key value of the record to delete
     * @return the number of affected rows
     * @throws IllegalArgumentException if the table does not have a primary key
     */
    @Transactional
    public int deleteById(ID id) {
        if (table.getPrimaryKey() == null) {
            throw new IllegalArgumentException("This method can only be called on tables with a primary key");
        }
        return dslContext.deleteFrom(table)
                .where(eq(table.getPrimaryKey(), id))
                .execute();
    }

    /**
     * Deletes records from the database that match the given condition.
     *
     * @param condition the condition to filter which records to delete
     * @return the number of affected rows
     */
    @Transactional
    public int delete(Condition condition) {
        return dslContext
                .deleteFrom(table)
                .where(condition).execute();
    }

    /**
     * Generates a condition to be used in database queries to match a given primary key and its value.
     * Inspired by {@link org.jooq.impl.DAOImpl}
     *
     * @param pk the primary key of the table
     * @param id the value of the primary key to match
     * @return a Condition used to match the specified primary key and its value
     */
    @SuppressWarnings("unchecked")
    private Condition eq(UniqueKey<R> pk, ID id) {
        List<TableField<R, ?>> fields = pk.getFields();
        if (fields.size() == 1) {
            TableField<R, ?> first = fields.get(0);
            return ((Field<Object>) first).equal(id);
        } else {
            java.lang.reflect.Field[] recordFields = id.getClass().getDeclaredFields();
            Object[] idValues = new Object[recordFields.length];
            for (int i = 0; i < recordFields.length; i++) {
                try {
                    java.lang.reflect.Field recordField = recordFields[i];
                    recordField.setAccessible(true);
                    idValues[i] = recordField.get(id);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            return row(pk.getFieldsArray()).equal(idValues);
        }
    }

}
