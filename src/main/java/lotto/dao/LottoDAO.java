package lotto.dao;

import lotto.domain.Lotto;
import lotto.domain.Lottos;
import lotto.util.ConvertLottoNumber;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LottoDAO {
    private Connection con;

    public LottoDAO(Connection con) {
        this.con = con;
    }

    public void addLottos(String lottoId, Lottos lottos) throws SQLException {
        String query = "INSERT INTO lotto VALUES (?, ?)";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setString(1, lottoId);
        StringBuilder stringLottos = new StringBuilder();
        List<String> strings = new ArrayList<>();
        for (Lotto lotto : lottos.getLottos()) {
            strings.add(lotto.toString());
        }
        stringLottos.append(String.join("\n", strings));
        pstmt.setString(2, stringLottos.toString());
        pstmt.executeUpdate();
    }

    public LottosDTO findByLottoId(String lottoId) throws SQLException {
        String query = "SELECT * FROM lotto WHERE lotto_id = ?";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setString(1, lottoId);
        ResultSet rs = pstmt.executeQuery();

        if (!rs.next()) return null;

        List<Lotto> lottos = makeLottos(rs.getString("lottos").split("\n"));

        LottosDTO lottosDTO = new LottosDTO();
        lottosDTO.setLottos(new Lottos(lottos));
        return lottosDTO;
    }

    private List<Lotto> makeLottos(String[] stringLottos) {
        List<Lotto> lottos = new ArrayList<>();
        for (String lotto : stringLottos) {
            lotto = lotto.substring(1, lotto.lastIndexOf("]"));
            lottos.add(new Lotto(ConvertLottoNumber.run(lotto)));
        }
        return lottos;
    }

    public void deleteLotto(String lottoId) throws SQLException {
        String query = "DELETE FROM lotto WHERE lotto_id = ?";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setString(1, lottoId);
        pstmt.executeUpdate();
    }

    public Integer getRound() throws SQLException {
        String query = "SELECT lotto_id FROM lotto ORDER BY lotto_id DESC LIMIT 1";
        PreparedStatement pstmt = con.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();

        if (!rs.next()) return 1;

        return Integer.parseInt(rs.getString("lotto_id")) + 1;
    }
}
