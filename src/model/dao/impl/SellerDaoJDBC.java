package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

	private Connection conn;
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	public SellerDaoJDBC() {
	}
	
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Seller obj) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("INSERT INTO seller\r\n" + 
					"(Name, Email, BirthDate, BaseSalary, DepartmentId)\r\n" + 
					"VALUES\r\n" + 
					"(?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			
			int rowsAffected = st.executeUpdate();
			
			if(rowsAffected > 0) {
				rs = st.getGeneratedKeys();
				while (rs.next()) {
					int id = rs.getInt(1);
					System.out.println("Done! ID = " + id);
				}
			}else {
				System.out.println("No rows affected");
			}	
		}		
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("UPDATE seller\r\n" + 
					"SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ?\r\n" + 
					"WHERE Id = ?");
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			st.setInt(6, obj.getId());
			
			st.executeUpdate();	
		}		
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("DELETE FROM seller WHERE Id = ?");
			st.setInt(1, id);
			st.executeUpdate();	
		}		
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT seller.*,department.Name as DepName\r\n" + 
					"FROM seller INNER JOIN department\r\n" + 
					"ON seller.DepartmentId = department.Id\r\n" + 
					"WHERE seller.Id = ?");
			
			st.setInt(1, id);
			rs = st.executeQuery();
			
			if (rs.next()) {
				Department dep = instanteateDepartment(rs);
				Seller obj = instanteateSeller(rs, dep);
				return obj;
			}
			return null;			
		}		
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Seller instanteateSeller(ResultSet rs, Department dep) throws SQLException{
		Seller obj = new Seller();
		obj.setName(rs.getString("Name"));
		obj.setId(rs.getInt("Id"));
		obj.setEmail(rs.getString("Email"));
		obj.setBaseSalary(rs.getDouble("BaseSalary"));
		obj.setBirthDate(new java.util.Date(rs.getDate("BirthDate").getTime()));
		obj.setDepartment(dep);
		
		return obj;
	}

	private Department instanteateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		
		return dep;
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		List<Seller> sellers = new ArrayList<>();
		try {
			st = conn.prepareStatement("SELECT seller.*,department.Name as DepName\r\n" + 
					"FROM seller INNER JOIN department\r\n" + 
					"ON seller.DepartmentId = department.Id\r\n" +
					"ORDER BY Name");

			rs = st.executeQuery();
			
			while (rs.next()) {
				Department dep = instanteateDepartment(rs);
				sellers.add(instanteateSeller(rs, dep));
			}
			return sellers;			
		}		
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Seller> findByDepartment(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		List<Seller> sellers = new ArrayList<>();
		try {
			st = conn.prepareStatement("SELECT seller.*,department.Name as DepName\r\n" + 
					"FROM seller INNER JOIN department\r\n" + 
					"ON seller.DepartmentId = department.Id\r\n" + 
					"WHERE Department.Id = ?\r\n" +
					"ORDER BY Name");
			
			st.setInt(1, id);
			rs = st.executeQuery();
			
			if (rs.next()) {
				Department dep = instanteateDepartment(rs);
				sellers.add(instanteateSeller(rs, dep));
				while(rs.next()) {
					sellers.add(instanteateSeller(rs, dep));
				}
				return sellers;
			}
			return null;			
		}		
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
}
